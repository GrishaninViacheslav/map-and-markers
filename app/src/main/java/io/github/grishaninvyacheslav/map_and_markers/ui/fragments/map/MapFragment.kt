package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.SupportMapFragment
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMapBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.BottomNavigationActivity
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.IBottomNavigation
import io.github.grishaninvyacheslav.map_and_markers.ui.views.ProgressButton
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.MapUseCase
import io.github.grishaninvyacheslav.map_and_markers.view_models.map.GoogleMapState
import io.github.grishaninvyacheslav.map_and_markers.view_models.map.LocationPositioningState
import io.github.grishaninvyacheslav.map_and_markers.view_models.map.MapViewModel
import io.github.grishaninvyacheslav.map_and_markers.view_models.map.MapViewModelFactory
import kotlin.properties.Delegates

class MapFragment : GeoLocationFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(MapUseCase().apply {
            MapAndMarkersApp.instance.appComponent.inject(this)
        })
    }

    private val newMarkerTitleInputView by lazy { EditText(requireContext()) }

    private val newMarkerTitleDialog by lazy {
        requireActivity().let {
            AlertDialog.Builder(it)
        }.apply {
            setView(newMarkerTitleInputView)
            setTitle(R.string.marker_title)
            setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                isTitleDialogShown = false
                dialog.dismiss()
            }
            setPositiveButton(
                R.string.confirm
            ) { dialog, id ->
                viewModel.addMarker(newMarkerTitleInputView.text.toString())
                isTitleDialogShown = false
                dialog.dismiss()
                dismissEditOptions()
            }
        }.create()
    }

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isEditOptionShown", isEditOptionShown)
        outState.putBoolean("isTitleDialogShown", isTitleDialogShown)
        outState.putString("titleInputValue", newMarkerTitleInputView.text.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("[MYLOG]", "MapFragment onViewCreated")
        initCallBacks()
        initViewModelStates()
        initListeners()
        initLiveDataObservers()
        restoreViewState(savedInstanceState)
    }

    override fun onFabClick() = showEditOptions()

    private var isTitleDialogShown by Delegates.notNull<Boolean>()
    override fun onActionConfirm(){
        isTitleDialogShown = true
        newMarkerTitleDialog.show()
    }

    override fun onCancelAction() = dismissEditOptions()

    private fun initCallBacks() {
        networkSwitchStateCallback = { isNetworkTurnedOn ->
            viewModel.networkSwitchedState(isNetworkTurnedOn)
        }
        gpsSwitchStateCallback = { isGpsTurnedOn ->
            viewModel.gpsSwitchedState(isGpsTurnedOn)
        }
    }

    private fun restoreViewState(savedInstanceState: Bundle?){
        isEditOptionShown = savedInstanceState?.getBoolean("isEditOptionShown") ?: false
        if(isEditOptionShown){
            showEditOptions()
        }
        isTitleDialogShown = savedInstanceState?.getBoolean("isTitleDialogShown") ?: false
        if(isTitleDialogShown){
            newMarkerTitleDialog.show()
        }
        savedInstanceState?.getString("titleInputValue")?.let {
            newMarkerTitleInputView.setText(it)
        }
    }

    private fun initViewModelStates() {
        viewModel.networkSwitchedState(isNetworkSourceAvailable())
        viewModel.gpsSwitchedState(isGpsSourceAvailable())
    }

    private fun initListeners() = with(binding) {
        updateNetworkLocation.setOnClickListener {
            if (!requestNetworkProviderIfNeeded()) {
                viewModel.updateNetworkLocation()
            }
        }
        updateGpsLocation.setOnClickListener {
            if (!requestGpsProviderIfNeeded()) {
                viewModel.updateGpsLocation()
            }
        }
        showMyLastBestLocation.setOnClickListener {
            if (!requestNetworkProviderIfNeeded() && !requestGpsProviderIfNeeded()) {
                viewModel.showLastBestKnownLocation()
            }
        }
    }

    private fun initLiveDataObservers() {
        viewModel.googleMapState.observe(viewLifecycleOwner) { renderGoogleMapState(it) }
        viewModel.apiErrorState.observe(viewLifecycleOwner) { renderApiErrorState(it) }
        viewModel.lastLocationState.observe(viewLifecycleOwner) { renderLastLocationState(it) }
        viewModel.gpsPositioningState.observe(viewLifecycleOwner) { renderGpsPositionState(it) }
        viewModel.networkPositioningState.observe(viewLifecycleOwner) {
            renderNetworkPositionState(
                it
            )
        }
    }


    private fun renderGoogleMapState(state: GoogleMapState) = when (state) {
        is GoogleMapState.Initializing -> {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(state.onMapReadyCallback)
        }
    }

    private fun renderApiErrorState(error: String) =
        Toast.makeText(requireContext(), "Произошла ошибка API:\n$error", Toast.LENGTH_LONG).show()

    private fun renderLastLocationState(state: LocationPositioningState) =
        renderProgressButtonState(binding.showMyLastBestLocation, state)

    private fun renderNetworkPositionState(state: LocationPositioningState) =
        renderProgressButtonState(binding.updateNetworkLocation, state)

    private fun renderGpsPositionState(state: LocationPositioningState) =
        renderProgressButtonState(binding.updateGpsLocation, state)

    private fun renderProgressButtonState(
        progressButton: ProgressButton,
        state: LocationPositioningState
    ) = when (state) {
        LocationPositioningState.Positioning -> progressButton.showState(ProgressButton.ProgressState.IN_PROGRESS)
        LocationPositioningState.Unavailable -> progressButton.showState(ProgressButton.ProgressState.UNAVAILABLE)
        LocationPositioningState.Steady -> progressButton.showState(ProgressButton.ProgressState.STEADY)
    }


    private var isEditOptionShown by Delegates.notNull<Boolean>()
    fun showEditOptions() {
        isEditOptionShown = true
        (requireActivity() as IBottomNavigation).hideNavigation()
        binding.marker.isVisible = true
    }

    private fun dismissEditOptions() {
        isEditOptionShown = false
        (requireActivity() as BottomNavigationActivity).showNavigation()
        binding.marker.isVisible = false
    }
}
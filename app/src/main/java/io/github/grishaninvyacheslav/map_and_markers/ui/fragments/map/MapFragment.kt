package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.SupportMapFragment
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.ui.views.ProgressButton
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.MapUseCase
import io.github.grishaninvyacheslav.map_and_markers.view_models.map.*

class MapFragment : MarkerCreatorFragment() {
    companion object {
        const val TITLE_INPUT_VALUE_KEY = "TITLE_INPUT_VALUE_KEY"
        const val IS_TITLE_DIALOG_SHOWN_KEY = "IS_TITLE_DIALOG_SHOWN"
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCallBacks()
        initViewModelStates()
        initListeners()
        initLiveDataObservers()
        restoreViewState(savedInstanceState)
    }

    override fun onFabClick() = showEditOptions()

    override fun onActionConfirm() = newMarkerTitleDialog.show()

    override fun onCancelAction() = dismissEditOptions()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_INPUT_VALUE_KEY, newMarkerTitleDialog.view.text.toString())
        outState.putBoolean(IS_TITLE_DIALOG_SHOWN_KEY, newMarkerTitleDialog.isShowing())
    }

    private fun restoreViewState(savedInstanceState: Bundle?) {
        savedInstanceState?.getString(TITLE_INPUT_VALUE_KEY)?.let {
            newMarkerTitleDialog.view.setText(it)
        }
        if (savedInstanceState?.getBoolean(IS_TITLE_DIALOG_SHOWN_KEY) == true) {
            newMarkerTitleDialog.show()
        }
    }

    private fun initCallBacks() {
        networkSwitchStateCallback = { isNetworkTurnedOn ->
            viewModel.networkSwitchedState(isNetworkTurnedOn)
        }
        gpsSwitchStateCallback = { isGpsTurnedOn ->
            viewModel.gpsSwitchedState(isGpsTurnedOn)
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
            if (!requestGpsProviderIfNeeded()) {
                viewModel.showLastBestKnownLocation()
            }
        }
    }

    private fun initLiveDataObservers() {
        viewModel.googleMapState.observe(viewLifecycleOwner) { renderGoogleMapState(it) }
        viewModel.apiErrorState.observe(viewLifecycleOwner) { showErrorMessage(it) }
        viewModel.localRepositoryState.observe(viewLifecycleOwner) {
            if (it is LocalRepositoryState.Error) {
                showErrorMessage(it.message)
            }
        }
        viewModel.networkPositioningState.observe(viewLifecycleOwner) {
            renderNetworkPositionState(
                it
            )
        }
        viewModel.gpsPositioningState.observe(viewLifecycleOwner) { renderGpsPositionState(it) }
        viewModel.lastLocationState.observe(viewLifecycleOwner) { renderLastLocationState(it) }
    }

    private fun renderGoogleMapState(state: GoogleMapState) = when (state) {
        is GoogleMapState.Error -> with(binding) {
            updateNetworkLocation.visibility = INVISIBLE
            updateGpsLocation.visibility = INVISIBLE
            showMyLastBestLocation.visibility = INVISIBLE
            progressBar.visibility = INVISIBLE
            showErrorMessage(state.message)
        }
        GoogleMapState.Loading -> with(binding) {
            updateNetworkLocation.visibility = INVISIBLE
            updateGpsLocation.visibility = INVISIBLE
            showMyLastBestLocation.visibility = INVISIBLE
            progressBar.visibility = VISIBLE
        }
        is GoogleMapState.Ready -> with(binding) {
            updateNetworkLocation.visibility = VISIBLE
            updateGpsLocation.visibility = VISIBLE
            showMyLastBestLocation.visibility = VISIBLE
            progressBar.visibility = INVISIBLE
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync(state.onMapReadyCallback)
        }
    }

    private fun showErrorMessage(errorMessage: String?) {
        val message =
            if (errorMessage == null) getString(R.string.unspecified_error) else String.format(
                getString(R.string.specified_error),
                errorMessage
            )
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun renderNetworkPositionState(state: LocationPositioningState) =
        renderProgressButtonState(binding.updateNetworkLocation, state)

    private fun renderGpsPositionState(state: LocationPositioningState) =
        renderProgressButtonState(binding.updateGpsLocation, state)

    private fun renderLastLocationState(state: LocationPositioningState) =
        renderProgressButtonState(binding.showMyLastBestLocation, state)

    private fun renderProgressButtonState(
        progressButton: ProgressButton,
        state: LocationPositioningState
    ) = when (state) {
        LocationPositioningState.Positioning -> progressButton.showState(ProgressButton.ProgressState.IN_PROGRESS)
        LocationPositioningState.Unavailable -> progressButton.showState(ProgressButton.ProgressState.UNAVAILABLE)
        LocationPositioningState.Steady -> progressButton.showState(ProgressButton.ProgressState.STEADY)
    }

    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(MapUseCase().apply {
            MapAndMarkersApp.instance.appComponent.inject(this)
        })
    }

    private val newMarkerTitleDialog by lazy {
        NewMarkerTitleDialog(requireContext()) { markerName ->
            viewModel.addMarker(markerName)
            dismissEditOptions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        newMarkerTitleDialog.dismiss()
    }
}
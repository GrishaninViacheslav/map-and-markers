package io.github.grishaninvyacheslav.map_and_markers.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.SupportMapFragment
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMapBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.views.ProgressButton
import io.github.grishaninvyacheslav.map_and_markers.view_models.GoogleMapState
import io.github.grishaninvyacheslav.map_and_markers.view_models.LocationPositioningState
import io.github.grishaninvyacheslav.map_and_markers.view_models.MapViewModel

class MapFragment : GeoLocationFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
    private val viewModel: MapViewModel by viewModels()

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCallBacks()
        initViewModelStates()
        initListeners()
        initLiveDataObservers()
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
            if (!requestNetworkProviderIfNeeded() && !requestGpsProviderIfNeeded()) {
                viewModel.showLastBestKnownLocation()
            }
        }
    }

    private fun initLiveDataObservers(){
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
}
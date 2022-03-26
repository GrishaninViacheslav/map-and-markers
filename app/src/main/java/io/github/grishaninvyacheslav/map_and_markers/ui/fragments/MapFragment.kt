package io.github.grishaninvyacheslav.map_and_markers.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.SupportMapFragment
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMapBinding
import io.github.grishaninvyacheslav.map_and_markers.view_models.GoogleMapState
import io.github.grishaninvyacheslav.map_and_markers.view_models.MapViewModel

class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
    private val viewModel: MapViewModel by viewModels()

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.googleMapState.observe(viewLifecycleOwner) { renderGoogleMapState(it) }
    }

    private fun renderGoogleMapState(state: GoogleMapState){
        when(state){
            is GoogleMapState.Initializing -> {
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(state.onMapReadyCallback)
            }
        }
    }
}
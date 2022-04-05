package io.github.grishaninvyacheslav.map_and_markers.view_models.map

import com.google.android.gms.maps.OnMapReadyCallback

sealed class GoogleMapState {
    object Loading: GoogleMapState()
    data class Error(val message: String): GoogleMapState()
    data class Ready(val onMapReadyCallback: OnMapReadyCallback) : GoogleMapState()
}
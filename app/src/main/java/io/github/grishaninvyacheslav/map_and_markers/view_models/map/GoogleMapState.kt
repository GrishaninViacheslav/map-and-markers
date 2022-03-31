package io.github.grishaninvyacheslav.map_and_markers.view_models.map

import com.google.android.gms.maps.OnMapReadyCallback

sealed class GoogleMapState {
    data class Initializing(val onMapReadyCallback: OnMapReadyCallback) : GoogleMapState()
}
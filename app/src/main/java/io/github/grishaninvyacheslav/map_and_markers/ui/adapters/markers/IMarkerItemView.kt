package io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers

import com.google.android.gms.maps.model.LatLng

interface IMarkerItemView {
    var pos: Int
    fun setTitle(title: String)
    fun setCoordinates(coordinates: LatLng)
}
package io.github.grishaninvyacheslav.map_and_markers.models.repositories

import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData

interface IMarkersRepository {
    fun saveMarker(marker: MarkerOptions)
    fun getMarkers(): MutableListLiveData<MarkerOptions>
    fun renameMarker(index: Int, newName: String)
    fun removeMarker(index: Int)
}
package io.github.grishaninvyacheslav.map_and_markers.models.repositories

import android.util.Log
import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData
import javax.inject.Inject

class MarkersRepository @Inject constructor() : IMarkersRepository {

    private val markersBuffer: MutableListLiveData<MarkerOptions> = MutableListLiveData<MarkerOptions>().apply {
        postValue(mutableListOf())
    }

    override fun saveMarker(marker: MarkerOptions) {
        markersBuffer.add(marker)
    }

    override fun getMarkers(): MutableListLiveData<MarkerOptions> {
        return markersBuffer
    }

    override fun renameMarker(index: Int, newName: String) {
        markersBuffer.set(index, markersBuffer[index].title(newName))
        Log.d("[MYLOG]", "renameMarker markersBuffer[indexOfMarkerToRename].title(${markersBuffer[index].title})")
    }

    override fun removeMarker(index: Int) {
        markersBuffer.removeAt(index)
    }
}
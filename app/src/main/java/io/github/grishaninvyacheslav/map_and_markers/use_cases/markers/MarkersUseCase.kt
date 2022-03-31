package io.github.grishaninvyacheslav.map_and_markers.use_cases.markers

import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import javax.inject.Inject

class MarkersUseCase {
    @Inject
    lateinit var markersRepository: IMarkersRepository

    fun saveMarker(marker: MarkerOptions) = markersRepository.saveMarker(marker)
    fun getMarkers() = markersRepository.getMarkers()
    fun renameMarker(indexOfMarkerToRename: Int, newName: String) = markersRepository.renameMarker(indexOfMarkerToRename, newName)
    fun removeMarker(indexOfMarkerToRemove: Int) = markersRepository.removeMarker(indexOfMarkerToRemove)
}
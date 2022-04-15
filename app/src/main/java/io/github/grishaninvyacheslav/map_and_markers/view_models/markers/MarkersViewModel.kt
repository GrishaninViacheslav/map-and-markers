package io.github.grishaninvyacheslav.map_and_markers.view_models.markers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.use_cases.markers.MarkersUseCase

class MarkersViewModel(private val markersUseCase: MarkersUseCase) : ViewModel() {
    private val mutableMarkerToRenameNameState: MutableLiveData<Pair<Int, String>> =
        MutableLiveData()
    private val mutableMarkerToRemoveNameState: MutableLiveData<Pair<Int, String>> =
        MutableLiveData()

    val markersListState = markersUseCase.getMarkers()

    val markerToRenameNameState: LiveData<Pair<Int, String>> = mutableMarkerToRenameNameState
    val markerToRemoveNameState: LiveData<Pair<Int, String>> = mutableMarkerToRemoveNameState

    fun requestMarkerRename(indexOfMarkerToRename: Int) {
        mutableMarkerToRenameNameState.value = Pair(
            indexOfMarkerToRename,
            markersListState[indexOfMarkerToRename].second.title
                ?: MapAndMarkersApp.instance.resources.getString(
                    R.string.marker_without_title
                )
        )
    }

    fun renameMarker(indexOfMarkerToRename: Int, newName: String) {
        markersUseCase.renameMarker(indexOfMarkerToRename, newName)
            .subscribe({ /* TODO(NOT YET IMPLEMENTED) */ }, { /* TODO(NOT YET IMPLEMENTED) */ })
    }

    fun requestMarkerRemove(indexOfMarkerToRemove: Int) {
        mutableMarkerToRemoveNameState.value = Pair(
            indexOfMarkerToRemove,
            markersListState[indexOfMarkerToRemove].second.title
                ?: MapAndMarkersApp.instance.resources.getString(
                    R.string.marker_without_title
                )
        )
    }

    fun removeMarker(indexOfMarkerToRemove: Int) {
        markersUseCase.removeMarker(indexOfMarkerToRemove)
            .subscribe({ /* TODO(NOT YET IMPLEMENTED) */ }, { /* TODO(NOT YET IMPLEMENTED) */ })
    }
}
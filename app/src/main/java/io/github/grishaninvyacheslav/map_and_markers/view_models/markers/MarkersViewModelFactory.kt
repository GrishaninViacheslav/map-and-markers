package io.github.grishaninvyacheslav.map_and_markers.view_models.markers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.grishaninvyacheslav.map_and_markers.use_cases.markers.MarkersUseCase

class MarkersViewModelFactory(private val markersUseCase: MarkersUseCase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MarkersViewModel(markersUseCase) as T
    }
}
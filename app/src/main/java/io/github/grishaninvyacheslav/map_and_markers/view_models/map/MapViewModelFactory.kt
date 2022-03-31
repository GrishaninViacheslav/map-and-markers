package io.github.grishaninvyacheslav.map_and_markers.view_models.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.MapUseCase

class MapViewModelFactory(private val mapUseCase: MapUseCase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(mapUseCase) as T
    }
}
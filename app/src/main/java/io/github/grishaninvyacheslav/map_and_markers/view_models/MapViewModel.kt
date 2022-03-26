package io.github.grishaninvyacheslav.map_and_markers.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.grishaninvyacheslav.map_and_markers.use_cases.MapUseCase

class MapViewModel(private val mapUseCase: MapUseCase = MapUseCase()) :
    ViewModel() {
    private val liveGoogleGoogleMapState: MutableLiveData<GoogleMapState> = MutableLiveData()

    val googleMapState: LiveData<GoogleMapState>
        get() {
            liveGoogleGoogleMapState.value =
                GoogleMapState.Initializing(mapUseCase.onMapReadyCallback)
            return liveGoogleGoogleMapState
        }
}
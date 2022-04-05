package io.github.grishaninvyacheslav.map_and_markers.models.repositories

import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData
import io.reactivex.rxjava3.core.Completable

interface IMarkersRepository {
    fun saveMarker(marker: MarkerOptions)
    fun getMarkersBuffer(): MutableListLiveData<Pair<String, MarkerOptions>>
    fun renameMarker(index: Int, newName: String)
    fun removeMarker(index: Int)
}
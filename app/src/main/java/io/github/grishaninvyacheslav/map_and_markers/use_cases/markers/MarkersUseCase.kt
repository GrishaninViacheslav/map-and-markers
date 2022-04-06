package io.github.grishaninvyacheslav.map_and_markers.use_cases.markers

import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MarkersUseCase {
    @Inject
    lateinit var markersRepository: IMarkersRepository

    fun getMarkers() = markersRepository.getMarkers()

    fun renameMarker(indexOfMarkerToRename: Int, newName: String) = Completable.fromCallable {
        markersRepository.renameMarker(indexOfMarkerToRename, newName)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun removeMarker(indexOfMarkerToRemove: Int) = Completable.fromCallable{
        markersRepository.removeMarker(indexOfMarkerToRemove)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
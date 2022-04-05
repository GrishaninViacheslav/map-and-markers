package io.github.grishaninvyacheslav.map_and_markers.models.repositories

import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.MarkersDao
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.toMarkerEntity
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.toMarkerOptions
import javax.inject.Inject

class MarkersRepository @Inject constructor(private val markersDao: MarkersDao) :
    IMarkersRepository {

    private val markersBuffer: MutableListLiveData<Pair<String, MarkerOptions>> = MutableListLiveData()

    override fun saveMarker(marker: MarkerOptions) {
        with(marker.toMarkerEntity()){
            markersDao.insertAll(this)
            markersBuffer.add(Pair(this.uid, marker))
        }
    }

    override fun getMarkersBuffer(): MutableListLiveData<Pair<String, MarkerOptions>> {
        if (!markersBuffer.isCollectionNotNull()) {
            markersBuffer.postValue(markersDao.getAll().map { Pair(it.uid, it.toMarkerOptions()) }.toMutableList())
        }
        return markersBuffer
    }

    override fun renameMarker(index: Int, newName: String) {
        with(markersBuffer[index].apply { second.title(newName) }){
            markersDao.update(this.toMarkerEntity())
            markersBuffer.set(index,this)
        }

    }

    override fun removeMarker(index: Int) {
        markersDao.delete(markersBuffer[index].toMarkerEntity())
        markersBuffer.removeAt(index)
    }
}
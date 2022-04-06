package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import androidx.room.*

@Dao
interface MarkersDao {
    @Query("SELECT * FROM markerEntity")
    fun getAll(): List<MarkerEntity>

    @Insert
    fun insertAll(vararg markerEntities: MarkerEntity)

    @Update
    fun update(markerEntity: MarkerEntity)

    @Delete
    fun delete(markerEntity: MarkerEntity)
}
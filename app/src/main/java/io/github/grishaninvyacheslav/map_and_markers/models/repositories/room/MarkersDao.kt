package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import androidx.room.*

@Dao
interface MarkersDao {
    @Query("SELECT * FROM marker")
    fun getAll(): List<Marker>

    @Insert
    fun insertAll(vararg markers: Marker)

    @Update
    fun update(marker: Marker)

    @Delete
    fun delete(marker: Marker)
}
package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MarkerEntity::class], version = 1)
abstract class MarkersDatabase : RoomDatabase(), IMarkersDatabase {
    abstract override fun markersDao(): MarkersDao
}
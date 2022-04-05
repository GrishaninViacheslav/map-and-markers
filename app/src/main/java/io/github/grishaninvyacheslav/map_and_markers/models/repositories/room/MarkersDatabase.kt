package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Marker::class], version = 1)
abstract class MarkersDatabase : RoomDatabase() {
    abstract fun markersDao(): MarkersDao

    companion object{
        private var instance: MarkersDatabase? = null

        fun getInstance(context: Context): MarkersDatabase{
            if(instance == null){
                instance = Room.databaseBuilder(
                    context,
                    MarkersDatabase::class.java, "database-name"
                ).build()
            }
            return instance!!
        }
    }
}
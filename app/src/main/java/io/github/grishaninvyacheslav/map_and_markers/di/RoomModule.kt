package io.github.grishaninvyacheslav.map_and_markers.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.IMarkersDatabase
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.MarkersDao
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.MarkersDatabase
import javax.inject.Singleton

@Module
class RoomModule {
    private val markersDatabaseName = "markers-database"

    @Singleton
    @Provides
    fun provideMarkersDatabase(appContext: Context): IMarkersDatabase =
        Room.databaseBuilder(
            appContext,
            MarkersDatabase::class.java, markersDatabaseName
        ).build()

    @Singleton
    @Provides
    fun provideMarkersDao(markersDatabase: IMarkersDatabase): MarkersDao =
        markersDatabase.markersDao()
}
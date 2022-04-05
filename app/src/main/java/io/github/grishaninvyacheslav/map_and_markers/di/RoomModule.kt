package io.github.grishaninvyacheslav.map_and_markers.di

import dagger.Module
import dagger.Provides
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.MarkersDao
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.room.MarkersDatabase
import javax.inject.Singleton

@Module
class RoomModule {
    @Singleton
    @Provides
    fun provideMarkersDataBase(mapAndMarkersApp: MapAndMarkersApp): MarkersDatabase = MarkersDatabase.getInstance(mapAndMarkersApp.applicationContext)

    @Singleton
    @Provides
    fun provideMarkersDao(markersDatabase: MarkersDatabase): MarkersDao = markersDatabase.markersDao()
}
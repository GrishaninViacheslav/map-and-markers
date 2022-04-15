package io.github.grishaninvyacheslav.map_and_markers.di

import dagger.Binds
import dagger.Module
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.MarkersRepository
import javax.inject.Singleton

@Module
interface RepositoriesModule {
    @Singleton
    @Binds
    fun bindMarkersRepository(markersRepository: MarkersRepository): IMarkersRepository
}
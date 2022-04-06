package io.github.grishaninvyacheslav.map_and_markers.di

import dagger.Binds
import dagger.Module
import io.github.grishaninvyacheslav.map_and_markers.models.providers.current_location.CurrentLocationProvider
import io.github.grishaninvyacheslav.map_and_markers.models.providers.current_location.ICurrentLocationProvider
import javax.inject.Singleton

@Module
interface ProvidersModule {
    @Singleton
    @Binds
    fun bindLocationProvider(locationProvider: CurrentLocationProvider): ICurrentLocationProvider
}
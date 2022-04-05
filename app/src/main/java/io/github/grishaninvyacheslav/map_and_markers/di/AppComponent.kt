package io.github.grishaninvyacheslav.map_and_markers.di

import dagger.Component
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.MapUseCase
import io.github.grishaninvyacheslav.map_and_markers.use_cases.markers.MarkersUseCase
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RoomModule::class,
        RepositoriesModule::class
    ]
)
interface AppComponent {
    fun inject(mapUseCase: MapUseCase)
    fun inject(markersUseCase: MarkersUseCase)
}

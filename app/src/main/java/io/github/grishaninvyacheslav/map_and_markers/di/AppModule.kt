package io.github.grishaninvyacheslav.map_and_markers.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler

@Module
class AppModule(val app: MapAndMarkersApp) {

    @Provides
    fun provideApp(): MapAndMarkersApp {
        return app
    }

    @Provides
    fun provideApplicationResources(): Resources {
        return app.resources
    }

    @Provides
    fun provideUiScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
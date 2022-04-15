package io.github.grishaninvyacheslav.map_and_markers.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

@Module
class AppModule(val app: MapAndMarkersApp) {
    @Provides
    fun provideAppContext(): Context {
        return app.applicationContext
    }

    @Provides
    fun provideResources(): Resources {
        return app.resources
    }

    @Provides
    fun provideUiScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    fun provideBackgroundScheduler(): Scheduler = Schedulers.io()
}
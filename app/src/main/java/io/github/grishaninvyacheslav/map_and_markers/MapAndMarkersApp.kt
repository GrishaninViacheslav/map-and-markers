package io.github.grishaninvyacheslav.map_and_markers

import android.app.Application
import io.github.grishaninvyacheslav.map_and_markers.di.AppComponent
import io.github.grishaninvyacheslav.map_and_markers.di.AppModule
import io.github.grishaninvyacheslav.map_and_markers.di.DaggerAppComponent

class MapAndMarkersApp: Application() {
    companion object {
        lateinit var instance: MapAndMarkersApp
            private set
    }

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
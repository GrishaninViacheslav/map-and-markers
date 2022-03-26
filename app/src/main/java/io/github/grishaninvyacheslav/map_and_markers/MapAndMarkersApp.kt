package io.github.grishaninvyacheslav.map_and_markers

import android.app.Application

class MapAndMarkersApp: Application() {
    companion object {
        lateinit var instance: MapAndMarkersApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
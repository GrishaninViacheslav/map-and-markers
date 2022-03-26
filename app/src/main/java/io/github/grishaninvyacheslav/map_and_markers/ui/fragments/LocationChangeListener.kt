package io.github.grishaninvyacheslav.map_and_markers.ui.fragments

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

abstract class LocationChangeListener(private val locationChangedCallback: (Location) -> Unit) : LocationListener {

    override fun onLocationChanged(loc: Location) {
        locationChangedCallback(loc)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}
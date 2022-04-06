package io.github.grishaninvyacheslav.map_and_markers.models.providers.current_location

import android.location.Location
import android.location.LocationListener

interface ICurrentLocationProvider {
    fun getLastKnownLocation(): Location?
    fun requestCurrentNetworkLocation(locationListener: LocationListener)
    fun requestCurrentGpsLocation(locationListener: LocationListener)
}
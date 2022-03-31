package io.github.grishaninvyacheslav.map_and_markers.models.providers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.entities.NoKnownLastLocationException

class CurrentLocationProvider(
    private val context: Context = MapAndMarkersApp.instance.applicationContext,
    private val locationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager
) {
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Location {
        val providers: List<String> = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        if (bestLocation == null) {
            throw NoKnownLastLocationException()
        }
        return bestLocation
    }


    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(
        locationListener: LocationListener,
        locationProvider: String
    ) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getCurrentLocation_R(locationListener, locationProvider)
        } else {
            requestSingleUpdate(locationListener, locationProvider)
        }

    @SuppressLint("MissingPermission")
    fun requestCurrentNetworkLocation(
        locationListener: LocationListener
    ) = requestCurrentLocation(locationListener, LocationManager.NETWORK_PROVIDER)

    @SuppressLint("MissingPermission")
    fun requestCurrentGpsLocation(
        locationListener: LocationListener
    ) = requestCurrentLocation(locationListener, LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission", "NewApi")
    private fun getCurrentLocation_R(
        locationListener: LocationListener,
        locationProvider: String
    ) =
    // TODO: проверить на эмуляторе
        // TODO: разобраться как вызывать onProviderDisabled и onProviderEnabled
        locationManager.getCurrentLocation(
            locationProvider,
            null,
            context.mainExecutor,
            locationListener::onLocationChanged
        )


    @SuppressLint("MissingPermission")
    private fun requestSingleUpdate(
        locationListener: LocationListener,
        locationProvider: String
    ) =
        /**
         * https://developer.android.com/reference/android/location/LocationManager#requestSingleUpdate(java.lang.String,%20android.location.LocationListener,%20android.os.Looper)
         * This method was deprecated in API level 30.
         * Use getCurrentLocation(java.lang.String, android.os.CancellationSignal, java.util.concurrent.Executor, java.util.function.Consumer) instead as it does not carry a risk of extreme battery drain.
         */
        locationManager.requestSingleUpdate(
            locationProvider,
            locationListener,
            null
        );

}
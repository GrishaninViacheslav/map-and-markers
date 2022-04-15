package io.github.grishaninvyacheslav.map_and_markers.models.providers.current_location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import javax.inject.Inject

class CurrentLocationProvider @Inject constructor(
    private val context: Context
) : ICurrentLocationProvider {
    private val locationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Location? {
        val providers: List<String> = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return bestLocation
    }

    @SuppressLint("MissingPermission")
    override fun requestCurrentNetworkLocation(
        locationListener: LocationListener
    ) = requestCurrentLocation(locationListener, LocationManager.NETWORK_PROVIDER)

    @SuppressLint("MissingPermission")
    override fun requestCurrentGpsLocation(
        locationListener: LocationListener
    ) = requestCurrentLocation(locationListener, LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(
        locationListener: LocationListener,
        locationProvider: String
    ) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getCurrentLocation(locationListener, locationProvider)
        } else {
            requestSingleUpdate(locationListener, locationProvider)
        }

    @SuppressLint("MissingPermission", "NewApi")
    private fun getCurrentLocation(
        locationListener: LocationListener,
        locationProvider: String
    ) =
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
        )
}
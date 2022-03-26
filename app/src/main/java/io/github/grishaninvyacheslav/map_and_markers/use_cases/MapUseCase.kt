package io.github.grishaninvyacheslav.map_and_markers.use_cases

import android.location.Location
import android.location.LocationListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.models.CurrentLocationProvider
import io.github.grishaninvyacheslav.map_and_markers.entities.NoKnownLastLocationException
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MapUseCase(
    private val locationProvider: CurrentLocationProvider = CurrentLocationProvider(),
    private val defaultPosition: LatLng = LatLng(-34.0, 151.0)
) {
    private lateinit var mMap: GoogleMap

    val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val initialPosition = try {
            locationProvider.getLastKnownLocation().let { LatLng(it.latitude, it.longitude) }
        } catch (e: NoKnownLastLocationException) {
            defaultPosition
        }
        mMap.addMarker(MarkerOptions().position(initialPosition).title("Ваша прошлая позиция"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15f))
    }

    fun navigateTo(location: Location) {
        mMap.addMarker(
            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                .title("Ваша прошлая позиция")
        )
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 15f
            )
        )
    }

    fun updateNetworkLocation(locationListener: LocationListener) {
        locationProvider.requestCurrentNetworkLocation(locationListener)
    }

    fun updateGpsLocation(locationListener: LocationListener) {
        locationProvider.requestCurrentGpsLocation(locationListener)
    }

//    fun updateLocation(): Single<Location> = Single.fromCallable {
//        var location: Location? = null
//        locationProvider.requestCurrentLocation { location = it }
//        while (location == null) { } // TODO: как избежать такого?
//        return@fromCallable location
//    }

    fun getLastKnownLocation(): Single<Location> =
        Single.fromCallable { locationProvider.getLastKnownLocation() }
            .subscribeOn(Schedulers.io())
}


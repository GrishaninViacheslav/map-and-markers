package io.github.grishaninvyacheslav.map_and_markers.use_cases.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.models.providers.CurrentLocationProvider
import io.github.grishaninvyacheslav.map_and_markers.entities.NoKnownLastLocationException
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MapUseCase(
    private val locationProvider: CurrentLocationProvider = CurrentLocationProvider(),
    private val defaultCamera: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
        LatLng(0.0, 0.0),
        2f
    )
) {

    @Inject
    lateinit var markersRepository: IMarkersRepository

    private lateinit var gMap: GoogleMap
    private val markers = mutableListOf<Marker>()

    private var cameraCurrentPosition: CameraPosition? = null

    private fun getBitmapDescriptorFromVector(id: Int, context: Context): BitmapDescriptor {
        var vectorDrawable: Drawable = context.getDrawable(id)!!
        var h = (48 * MapAndMarkersApp.instance.resources.displayMetrics.density).toInt();
        var w = (48 * MapAndMarkersApp.instance.resources.displayMetrics.density).toInt();
        vectorDrawable.setBounds(0, 0, w, h)
        var bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

    private val markerIcon = getBitmapDescriptorFromVector(
        R.drawable.outline_place_24,
        MapAndMarkersApp.instance.applicationContext
    )

    val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        gMap = googleMap.apply {
            setOnCameraMoveListener {
                cameraCurrentPosition = cameraPosition
            }
        }

        val initialCamera =
            cameraCurrentPosition?.let { CameraUpdateFactory.newCameraPosition(it) }
                ?: try {
                    CameraUpdateFactory.newLatLngZoom(
                        locationProvider.getLastKnownLocation()
                            .let { LatLng(it.latitude, it.longitude) },
                        15f
                    )
                } catch (e: NoKnownLastLocationException) {
                    defaultCamera
                }
        gMap.moveCamera(initialCamera)
        cameraCurrentPosition = gMap.cameraPosition
        markersRepository.getMarkers().observeForever { updateMarkers(it) }
    }

    private fun updateMarkers(markerOptions: List<MarkerOptions>) {
        for (marker in markers) {
            marker.remove()
        }
        for (option in markerOptions) {
            gMap.addMarker(option)?.let { markers.add(it) }
        }
    }

    fun addMarkerOnCameraPosition(title: String) =
        cameraCurrentPosition?.let { cameraCurrentPosition ->
            gMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            cameraCurrentPosition.target.latitude,
                            cameraCurrentPosition.target.longitude
                        )
                    )
                    .title(title).icon(
                        markerIcon
                    ).also {
                        markersRepository.saveMarker(it)
                    }
            ).also {
                it?.let { markers.add(it) }
            }
        }

    fun removeMarker(marker: Marker) {

    }

    fun navigateTo(location: Location) {
        gMap.animateCamera(
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


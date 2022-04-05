package io.github.grishaninvyacheslav.map_and_markers.use_cases.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData
import io.github.grishaninvyacheslav.map_and_markers.models.providers.CurrentLocationProvider
import io.github.grishaninvyacheslav.map_and_markers.entities.NoKnownLastLocationException
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
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
    private val markersBuffer = mutableListOf<Marker>()

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

    var liveMarkersOptionsList: MutableListLiveData<Pair<String, MarkerOptions>>? = null
    var markerOptionsObserver: Observer<MutableList<Pair<String, MarkerOptions>>>? = null

    fun getMapReadyCallback(): Single<OnMapReadyCallback> = Single.fromCallable {
        Log.d("[BUG]", "getMapReadyCallback() Single.fromCallable")
        if (liveMarkersOptionsList == null) {
            liveMarkersOptionsList = markersRepository.getMarkersBuffer()
        }
        Log.d("[BUG]", "return@fromCallable OnMapReadyCallback")
        return@fromCallable OnMapReadyCallback { googleMap ->
            Log.d("[BUG]", "OnMapReadyCallback")
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
            Log.d("[BUG]", "markersRepository.getMarkersBuffer()")
            if (markerOptionsObserver == null) {
                markerOptionsObserver = MarkerOptionsObserver()
                liveMarkersOptionsList!!.observeForever(markerOptionsObserver!!)
            } else {
                updateMarkers(liveMarkersOptionsList!!.map { it.second })
            }
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    private fun updateMarkers(markerOptions: List<MarkerOptions>) {
        for (marker in markersBuffer) {
            marker.remove()
        }
        for (option in markerOptions) {
            option.icon(
                markerIcon
            )
            gMap.addMarker(option)?.let { markersBuffer.add(it) }
        }
    }

    fun addMarkerOnCameraPosition(title: String): Completable {
        val markerOptions = MarkerOptions()
            .position(
                LatLng(
                    cameraCurrentPosition!!.target.latitude,
                    cameraCurrentPosition!!.target.longitude
                )
            )
            .title(title).icon(
                markerIcon
            )
        Log.d("[FUCK]", "gMap.addMarker")
        val marker = gMap.addMarker(
            markerOptions
        )
            ?: return Completable.error(RuntimeException("При добавлении маркера на экземпляр Google карты произошла ошибка"))
        return Completable.fromCallable {
            try {
                Log.d("[FUCK]", "markersRepository.saveMarker(markerOptions)")
                markersRepository.saveMarker(markerOptions)
                Log.d("[FUCK]", "markersBuffer.add(marker)")
                markersBuffer.add(marker)
            } catch (e: Exception) {
                throw RuntimeException("Не удалось сохранить данные о маркере на устройстве")
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

    fun getLastKnownLocation(): Single<Location> =
        Single.fromCallable { locationProvider.getLastKnownLocation() }
            .subscribeOn(Schedulers.io())

    private inner class MarkerOptionsObserver : Observer<MutableList<Pair<String, MarkerOptions>>> {
        override fun onChanged(markers: MutableList<Pair<String, MarkerOptions>>) {
            updateMarkers(markers.map { it.second })
        }
    }

    fun onClear() {
        markerOptionsObserver?.let { liveMarkersOptionsList?.removeObserver(it) }
    }
}


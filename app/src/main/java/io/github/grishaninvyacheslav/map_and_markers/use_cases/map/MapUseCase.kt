package io.github.grishaninvyacheslav.map_and_markers.use_cases.map

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.entities.MutableListLiveData
import io.github.grishaninvyacheslav.map_and_markers.models.providers.current_location.CurrentLocationProvider
import io.github.grishaninvyacheslav.map_and_markers.entities.NoKnownLastLocationException
import io.github.grishaninvyacheslav.map_and_markers.entities.getBitmapDescriptorFromVector
import io.github.grishaninvyacheslav.map_and_markers.models.repositories.IMarkersRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
import javax.inject.Inject

class MapUseCase(
    private val defaultCamera: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
        LatLng(0.0, 0.0),
        2f
    )
) {
    @Inject
    lateinit var locationProvider: CurrentLocationProvider

    @Inject
    lateinit var markersRepository: IMarkersRepository

    @Inject
    lateinit var appContext: Context

    fun getMapReadyCallback(): Single<OnMapReadyCallback> = Single.fromCallable {
        if (liveMarkersOptionsList == null) {
            liveMarkersOptionsList = markersRepository.getMarkers()
        }
        return@fromCallable OnMapReadyCallback { googleMap ->
            gMap = googleMap.apply {
                setOnCameraMoveListener {
                    cameraCurrentPosition = cameraPosition
                }
            }
            val initialCamera =
                cameraCurrentPosition
                    ?.let { CameraUpdateFactory.newCameraPosition(it) }
                    ?: locationProvider.getLastKnownLocation()
                        ?.let {
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                15f
                            )
                        }
                        ?: defaultCamera
            gMap.moveCamera(initialCamera)
            cameraCurrentPosition = gMap.cameraPosition
            if (markerOptionsObserver == null) {
                markerOptionsObserver = MarkerOptionsObserver()
                liveMarkersOptionsList!!.observeForever(markerOptionsObserver!!)
            } else {
                updateMarkers(liveMarkersOptionsList!!.map { it.second })
            }
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun addMarkerOnCameraPosition(title: String): Completable {
        val markerOptions = MarkerOptions()
            .position(
                LatLng(
                    cameraCurrentPosition!!.target.latitude,
                    cameraCurrentPosition!!.target.longitude
                )
            )
            .title(title)
            .icon(markerIcon)
        val marker = gMap.addMarker(markerOptions)
            ?: return Completable.error(RuntimeException("При добавлении маркера на экземпляр Google карты произошла ошибка"))
        return Completable.fromCallable {
            try {
                markersRepository.saveMarker(markerOptions)
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
        Single.fromCallable {
            locationProvider.getLastKnownLocation() ?: throw NoKnownLastLocationException()
        }
            .subscribeOn(Schedulers.io())

    private inner class MarkerOptionsObserver : Observer<MutableList<Pair<String, MarkerOptions>>> {
        override fun onChanged(markers: MutableList<Pair<String, MarkerOptions>>) {
            updateMarkers(markers.map { it.second })
        }
    }

    private fun updateMarkers(markerOptions: List<MarkerOptions>) {
        for (marker in markersBuffer) {
            marker.remove()
        }
        for (option in markerOptions) {
            option.icon(markerIcon)
            gMap.addMarker(option)?.let { markersBuffer.add(it) }
        }
    }

    fun onClear() {
        markerOptionsObserver?.let { liveMarkersOptionsList?.removeObserver(it) }
    }

    private lateinit var gMap: GoogleMap
    private val markersBuffer = mutableListOf<Marker>()
    private var cameraCurrentPosition: CameraPosition? = null

    private var liveMarkersOptionsList: MutableListLiveData<Pair<String, MarkerOptions>>? = null
    private var markerOptionsObserver: Observer<MutableList<Pair<String, MarkerOptions>>>? = null

    private val markerIcon = getBitmapDescriptorFromVector(
        R.drawable.outline_place_24,
        // appContext // TODO: не понимаю почему при обращении к этому свойству выбрасывается иселючение kotlin.UninitializedPropertyAccessException: lateinit property appContext has not been initialized
        MapAndMarkersApp.instance.applicationContext
    )
}


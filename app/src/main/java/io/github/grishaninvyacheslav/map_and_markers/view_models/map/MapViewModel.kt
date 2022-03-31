package io.github.grishaninvyacheslav.map_and_markers.view_models.map

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.GMapApiErrorUseCase
import io.github.grishaninvyacheslav.map_and_markers.use_cases.map.MapUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable

class MapViewModel(
    private val mapUseCase: MapUseCase,
    private val apiErrorMessageUseCase: GMapApiErrorUseCase = GMapApiErrorUseCase(),
    var uiScheduler: Scheduler = AndroidSchedulers.mainThread()
) :
    ViewModel() {
    private val mutableGoogleGoogleMapState: MutableLiveData<GoogleMapState> = MutableLiveData()
    private val mutableLastBestLocationState: MutableLiveData<LocationPositioningState> =
        MutableLiveData()
    private val mutableNetworkPositioningState: MutableLiveData<LocationPositioningState> = MutableLiveData()
    private val mutableGpsPositioningState: MutableLiveData<LocationPositioningState> = MutableLiveData()

    val googleMapState: LiveData<GoogleMapState>
        get() {
            mutableGoogleGoogleMapState.value =
                GoogleMapState.Initializing(mapUseCase.onMapReadyCallback)
            return mutableGoogleGoogleMapState
        }

    val lastLocationState: LiveData<LocationPositioningState>
        get() {
            return mutableLastBestLocationState
        }

    val networkPositioningState: LiveData<LocationPositioningState>
        get() {
            return mutableNetworkPositioningState
        }

    val gpsPositioningState: LiveData<LocationPositioningState>
        get() {
            return mutableGpsPositioningState
        }

    val apiErrorState: LiveData<String> =
        LiveDataReactiveStreams.fromPublisher(apiErrorMessageUseCase.errorMessage.toFlowable())

    fun updateNetworkLocation(){
        if (mutableNetworkPositioningState.value == LocationPositioningState.Positioning) {
            return
        }
        mutableNetworkPositioningState.value = LocationPositioningState.Positioning
        mapUseCase.updateNetworkLocation(networkLocationListener)
    }

    fun updateGpsLocation() {
        if (mutableGpsPositioningState.value == LocationPositioningState.Positioning) {
            return
        }
        mutableGpsPositioningState.value = LocationPositioningState.Positioning
        mapUseCase.updateGpsLocation(gpsLocationListener)
    }

    fun showLastBestKnownLocation() {
        if (mutableLastBestLocationState.value == LocationPositioningState.Positioning) {
            return
        }
        lastKnownLocationExtraction = mapUseCase.getLastKnownLocation()
            .observeOn(uiScheduler)
            .subscribe({ location: Location ->
                mutableLastBestLocationState.value = LocationPositioningState.Steady
                mapUseCase.navigateTo(location)
            }, {
                mutableLastBestLocationState.value = LocationPositioningState.Unavailable
                updateNetworkLocation()
            })
        mutableLastBestLocationState.value = LocationPositioningState.Positioning
    }



    fun networkSwitchedState(isNetworkTurnedOn: Boolean){
        if(!isNetworkTurnedOn){
            mutableNetworkPositioningState.value = LocationPositioningState.Unavailable
            mutableLastBestLocationState.value = LocationPositioningState.Unavailable
        } else if(mutableNetworkPositioningState.value == LocationPositioningState.Unavailable){
            mutableNetworkPositioningState.value = LocationPositioningState.Steady
            mutableLastBestLocationState.value = LocationPositioningState.Steady
        }
    }

    fun gpsSwitchedState(isGpsTurnedOn: Boolean){
        if(!isGpsTurnedOn){
            mutableGpsPositioningState.value = LocationPositioningState.Unavailable
        } else if(mutableGpsPositioningState.value == LocationPositioningState.Unavailable) {
            mutableGpsPositioningState.value = LocationPositioningState.Steady
        }
    }

    fun addMarker(title: String){
        mapUseCase.addMarkerOnCameraPosition(title)
    }

    private lateinit var lastKnownLocationExtraction: Disposable

    // TODO: отрефакторить: копирует gpsLocationListener
    private val networkLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mutableNetworkPositioningState.value = LocationPositioningState.Steady
            when (mutableLastBestLocationState.value) {
                LocationPositioningState.Positioning -> {
                    lastKnownLocationExtraction.dispose()
                    mutableLastBestLocationState.value = LocationPositioningState.Steady
                    mapUseCase.navigateTo(location)
                }
                LocationPositioningState.Unavailable -> {
                    mutableLastBestLocationState.value = LocationPositioningState.Steady
                }
            }
        }

        override fun onProviderDisabled(provider: String) {
            mutableNetworkPositioningState.value = LocationPositioningState.Unavailable
            mutableLastBestLocationState.value = LocationPositioningState.Unavailable
        }
        override fun onProviderEnabled(provider: String) {
            mutableNetworkPositioningState.value = LocationPositioningState.Steady
            mutableLastBestLocationState.value = LocationPositioningState.Steady
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    private val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mutableGpsPositioningState.value = LocationPositioningState.Steady
            when (mutableLastBestLocationState.value) {
                LocationPositioningState.Positioning -> {
                    lastKnownLocationExtraction.dispose()
                    mutableLastBestLocationState.value = LocationPositioningState.Steady
                    mapUseCase.navigateTo(location)
                }
                LocationPositioningState.Unavailable -> {
                    mutableLastBestLocationState.value = LocationPositioningState.Steady
                }
            }
        }

        override fun onProviderDisabled(provider: String) {
            mutableGpsPositioningState.value = LocationPositioningState.Unavailable
            mutableLastBestLocationState.value = LocationPositioningState.Unavailable
        }
        override fun onProviderEnabled(provider: String) {
            mutableGpsPositioningState.value = LocationPositioningState.Steady
            mutableLastBestLocationState.value = LocationPositioningState.Steady
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }
}
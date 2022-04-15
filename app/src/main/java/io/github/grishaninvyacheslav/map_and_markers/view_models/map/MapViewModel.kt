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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class MapViewModel(
    private val mapUseCase: MapUseCase,
    private val apiErrorMessageUseCase: GMapApiErrorUseCase = GMapApiErrorUseCase(),
    private var uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val disposables: CompositeDisposable = CompositeDisposable()
) :
    ViewModel() {
    private val mutableGoogleGoogleMapState: MutableLiveData<GoogleMapState> = MutableLiveData()
    private val mutableLastBestLocationState: MutableLiveData<LocationPositioningState> =
        MutableLiveData()
    private val mutableNetworkPositioningState: MutableLiveData<LocationPositioningState> =
        MutableLiveData()
    private val mutableGpsPositioningState: MutableLiveData<LocationPositioningState> =
        MutableLiveData()

    private val mutableLocalRepositoryState: MutableLiveData<LocalRepositoryState> =
        MutableLiveData()

    val googleMapState: LiveData<GoogleMapState>
        get() {
            mutableGoogleGoogleMapState.value = GoogleMapState.Loading
            disposables.add(
                mapUseCase.getMapReadyCallback().subscribe({
                    mutableGoogleGoogleMapState.value = GoogleMapState.Ready(it)
                }, {
                    mutableGoogleGoogleMapState.value = GoogleMapState.Error(it.toString())
                })
            )
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

    val localRepositoryState: LiveData<LocalRepositoryState> = mutableLocalRepositoryState

    fun updateNetworkLocation() {
        if (mutableNetworkPositioningState.value == LocationPositioningState.Positioning) {
            return
        }
        mutableNetworkPositioningState.value = LocationPositioningState.Positioning
        mapUseCase.updateNetworkLocation(CurrentLocationListener(mutableNetworkPositioningState))
    }

    fun updateGpsLocation() {
        if (mutableGpsPositioningState.value == LocationPositioningState.Positioning) {
            return
        }
        mutableGpsPositioningState.value = LocationPositioningState.Positioning
        mapUseCase.updateGpsLocation(CurrentLocationListener(mutableGpsPositioningState))
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


    fun networkSwitchedState(isNetworkTurnedOn: Boolean) {
        if (!isNetworkTurnedOn) {
            mutableNetworkPositioningState.value = LocationPositioningState.Unavailable
            mutableLastBestLocationState.value = LocationPositioningState.Unavailable
        } else if (mutableNetworkPositioningState.value == LocationPositioningState.Unavailable) {
            mutableNetworkPositioningState.value = LocationPositioningState.Steady
            mutableLastBestLocationState.value = LocationPositioningState.Steady
        }
    }

    fun gpsSwitchedState(isGpsTurnedOn: Boolean) {
        if (!isGpsTurnedOn) {
            mutableGpsPositioningState.value = LocationPositioningState.Unavailable
        } else if (mutableGpsPositioningState.value == LocationPositioningState.Unavailable) {
            mutableGpsPositioningState.value = LocationPositioningState.Steady
        }
    }

    fun addMarker(title: String) {
        mutableLocalRepositoryState.value = LocalRepositoryState.InProgress
        disposables.add(
            mapUseCase.addMarkerOnCameraPosition(title)
                .subscribe({
                    mutableLocalRepositoryState.value = LocalRepositoryState.Success
                }, {
                    mutableLocalRepositoryState.value = LocalRepositoryState.Error(it.message)
                })
        )
    }

    private lateinit var lastKnownLocationExtraction: Disposable

    private inner class CurrentLocationListener(private val positioningState: MutableLiveData<LocationPositioningState>): LocationListener{
        override fun onLocationChanged(location: Location) {
            positioningState.value = LocationPositioningState.Steady
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
            positioningState.value = LocationPositioningState.Unavailable
            mutableLastBestLocationState.value = LocationPositioningState.Unavailable
        }

        override fun onProviderEnabled(provider: String) {
            positioningState.value = LocationPositioningState.Steady
            mutableLastBestLocationState.value = LocationPositioningState.Steady
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        mapUseCase.onClear()
    }
}
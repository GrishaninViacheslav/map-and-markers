//package io.github.grishaninvyacheslav.map_and_markers.ui.fragments
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.viewModels
//import com.google.android.gms.maps.MapsInitializer
//import com.google.android.gms.maps.SupportMapFragment
//import io.github.grishaninvyacheslav.map_and_markers.R
//import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMapBinding
//import io.github.grishaninvyacheslav.map_and_markers.view_models.GoogleMapState
//import io.github.grishaninvyacheslav.map_and_markers.view_models.MapViewModel
//import java.util.function.Consumer
//
//class _MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
//    private val viewModel: MapViewModel by viewModels()
//
//    companion object {
//        fun newInstance() = _MapFragment()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel.googleMapState.observe(viewLifecycleOwner) { renderGoogleMapState(it) }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    private fun renderGoogleMapState(state: GoogleMapState) {
//        when (state) {
//            is GoogleMapState.Initializing -> {
//                Log.d("[MYLOG]", "MapsInitializer: ${MapsInitializer.initialize(requireContext())}")
//
//                val locationManager =
//                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                val locationProvider = LocationManager.GPS_PROVIDER
//
//                if (ActivityCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                    ||
//                    ActivityCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//
//                    requestPermissions(
//                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                        404
//                    )
//                    return
//                }
//
//
//                val locationListener: LocationListener = LocationChangeListener()
////                locationManager.requestLocationUpdates(
////                    LocationManager.GPS_PROVIDER, 5000, 10F, locationListener
////                )
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    // TODO: проверить на эмуляторе
//                    locationManager.getCurrentLocation(
//                        LocationManager.GPS_PROVIDER,
//                        null,
//                        requireContext().getMainExecutor(),
//                        object : Consumer<Location?> {
//                            override fun accept(location: Location?) {
//                                Log.d("[MYLOG]",
//                                    "getCurrentLocation location changed: location($location)")
//                            }
//                        })
//                } else {
//                    // https://developer.android.com/reference/android/location/LocationManager#requestSingleUpdate(java.lang.String,%20android.location.LocationListener,%20android.os.Looper)
//                    // This method was deprecated in API level 30.
//                    // Use getCurrentLocation(java.lang.String, android.os.CancellationSignal, java.util.concurrent.Executor, java.util.function.Consumer) instead as it does not carry a risk of extreme battery drain.
//                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
//                }
//
//
//
//
//                val lastKnownLocation = //getLastKnownLocation()
//                    locationManager.getLastKnownLocation(locationProvider)
//                Log.d("[MYLOG]", "lastKnownLocation($lastKnownLocation)")
//                val mapFragment =
//                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//                mapFragment.
//
//                mapFragment.getMapAsync(state.onMapReadyCallback)
//            }
//        }
//    }
//
//    lateinit var mLocationManager: LocationManager
//
//    private fun getLastKnownLocation(): Location? {
//        mLocationManager = requireContext().getSystemService(
//            LOCATION_SERVICE
//        ) as LocationManager
//        val providers: List<String> = mLocationManager.getProviders(true)
//        var bestLocation: Location? = null
//        for (provider in providers) {
//            val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l
//            }
//        }
//        return bestLocation
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>, grantResults: IntArray
//    ) {
//        //checkPermissionsResult(requestCode, grantResults)
//    }
//
//}
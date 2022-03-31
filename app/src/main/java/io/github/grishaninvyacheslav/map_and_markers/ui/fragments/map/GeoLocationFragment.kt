package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.fragments.BaseFragment


abstract class GeoLocationFragment<Binding : ViewBinding>(
    bindingFactory: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> Binding
) : BaseFragment<Binding>(bindingFactory) {

    private val locationManager by lazy { requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    protected var gpsSwitchStateCallback: ((isGpsTurnedOn: Boolean) -> Unit)? = null
    protected var networkSwitchStateCallback: ((isNetworkTurnedOn: Boolean) -> Unit)? = null

    private val locationSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("[MYLOG]", "onReceive")
            if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                Log.d("[MYLOG]", "intent.action != LocationManager.PROVIDERS_CHANGED_ACTION")
                gpsSwitchStateCallback?.invoke(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                networkSwitchStateCallback?.invoke(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().registerReceiver(
            locationSwitchStateReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        );
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(locationSwitchStateReceiver)
    }

    protected fun isGpsSourceAvailable() = (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            )

    protected fun isNetworkSourceAvailable() = (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            )

    protected fun requestNetworkProviderIfNeeded(): Boolean {
        if (requestPermissionIfNeeded(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true
        }
        return if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            true
        } else {
            false
        }
    }

    protected fun requestGpsProviderIfNeeded(): Boolean {
        if (requestPermissionIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true
        }
        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // TODO: если успеть два раза нажать по кнопке, то откроется две эти активити. Есть ли способ это исправить?
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) // Вместо этого можно использовать Google Settings Client: https://stackoverflow.com/a/54454201/18590550, https://developer.android.com/training/location/change-location-settings
            true
        } else {
            false
        }
    }
}
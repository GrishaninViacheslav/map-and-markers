package io.github.grishaninvyacheslav.map_and_markers.ui.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.BottomNavigationActivity

abstract class BaseFragment<Binding : ViewBinding>(
    private val bindingFactory: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> Binding
) : Fragment(), BottomNavigationActivity.FabListener {

    private var _binding: Binding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = bindingFactory(inflater, container, false).also { _binding = it }.root


    private val permissionRequest: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    protected fun requestPermissionIfNeeded(permission: String) = if (
        ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        permissionRequest.launch(permission)
        true
    } else {
        false
    }
}
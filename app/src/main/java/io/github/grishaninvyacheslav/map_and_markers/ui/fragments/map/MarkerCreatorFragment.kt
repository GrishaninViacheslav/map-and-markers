package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMapBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.BottomNavigationActivity
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.IBottomNavigation
import kotlin.properties.Delegates

abstract class MarkerCreatorFragment :
    GeoLocationFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {
    companion object {
        const val IS_EDIT_OPTIONS_SHOWN_KEY = "IS_EDIT_OPTIONS_SHOWN"
    }

    fun showEditOptions() {
        isEditOptionShown = true
        (requireActivity() as IBottomNavigation).hideNavigation()
        binding.marker.isVisible = true
    }

    protected fun dismissEditOptions() {
        isEditOptionShown = false
        (requireActivity() as BottomNavigationActivity).showNavigation()
        binding.marker.isVisible = false
    }

    private var isEditOptionShown by Delegates.notNull<Boolean>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_OPTIONS_SHOWN_KEY, isEditOptionShown)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        isEditOptionShown = savedInstanceState?.getBoolean(IS_EDIT_OPTIONS_SHOWN_KEY) ?: false
        if (isEditOptionShown) {
            showEditOptions()
        }
    }
}
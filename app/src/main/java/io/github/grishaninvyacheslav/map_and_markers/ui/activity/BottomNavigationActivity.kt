package io.github.grishaninvyacheslav.map_and_markers.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.ActivityBottomNavigationBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map.MapFragment
import io.github.grishaninvyacheslav.map_and_markers.ui.fragments.markers.MarkersFragment

class BottomNavigationActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
    IBottomNavigation {

    override fun hideNavigation() = with(binding) {
        bottomAppBar.performHide()
        fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                cancel.isVisible = true
                confirm.isVisible = true
                bottomAppBar.visibility = View.INVISIBLE // workaround бага https://github.com/material-components/material-components-android/issues/1361
            }
        })
    }

    override fun showNavigation() = with(binding) {
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.performShow()
        fab.show(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onShown(fab: FloatingActionButton?) {
                cancel.isVisible = false
                confirm.isVisible = false
            }
        })
    }

    override fun navigateTo(screens: Screens){
        openScreen(screens)
        binding.bottomNavigationView.menu.findItem(screens.menuId).isChecked = true
    }

    private lateinit var binding: ActivityBottomNavigationBinding

    private lateinit var currScreen: Screens

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("currScreen", currScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currScreen = savedInstanceState?.getParcelable("currScreen") ?: Screens.Map
        Log.d("[MYLOG]", "currScreen $currScreen")
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        initBottomAppBar()
        setContentView(binding.root)

    }

    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.screen_map -> openScreen(Screens.Map)
        R.id.screen_markers -> openScreen(Screens.Markers)
        else -> false
    }

    private fun initBottomAppBar() = with(binding) {
        cancel.isVisible = false
        confirm.isVisible = false
        bottomNavigationView.background = null
        bottomNavigationView.setOnItemSelectedListener(this@BottomNavigationActivity)
        bottomNavigationView.selectedItemId = currScreen.menuId
        Log.d("[MYLOG]", "bottomNavigationView.selectedItemId = ${currScreen.menuId == R.id.screen_map}")
        fab.setOnClickListener {
            (getCurrentFragment() as? FabListener)?.onFabClick()
        }
        confirm.setOnClickListener {
            (getCurrentFragment() as? FabListener)?.onActionConfirm()
        }
        cancel.setOnClickListener {
            (getCurrentFragment() as? FabListener)?.onCancelAction()
        }
    }

    private fun openScreen(nextScreen: Screens): Boolean {
        val currentFragment: Fragment? = getCurrentFragment()
        val fragmentToOpen = supportFragmentManager.findFragmentByTag(nextScreen.menuId.toString())
        if (currentFragment != null && fragmentToOpen != null && currentFragment === fragmentToOpen) return false
        val transaction = supportFragmentManager.beginTransaction().apply {
            if (currentFragment == null) {
                return@apply
            }
            if (currentFragment is MapFragment && nextScreen == Screens.Markers) {
                setCustomAnimations(
                    R.anim.slide_in_to_left,
                    R.anim.slide_out_to_left,
                    R.anim.slide_in_to_left,
                    R.anim.slide_out_to_left
                )
            } else if (currentFragment is MarkersFragment && (nextScreen == Screens.Map || nextScreen == Screens.MarkerCreator)) {
                setCustomAnimations(
                    R.anim.slide_in_to_right,
                    R.anim.slide_out_to_right,
                    R.anim.slide_in_to_right,
                    R.anim.slide_out_to_right
                )
            }
        }
        if (fragmentToOpen == null) {
            val newFragment = when (nextScreen) {
                Screens.Map -> MapFragment.newInstance()
                Screens.MarkerCreator -> MapFragment.newInstance()
                Screens.Markers -> MarkersFragment.newInstance()
            }
            transaction.add(
                R.id.fragment_container,
                newFragment, nextScreen.menuId.toString()
            )
        }
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        if (fragmentToOpen != null) {
            transaction.show(fragmentToOpen)
        }
        transaction.commitNow()
        if(nextScreen is Screens.MarkerCreator){
            (getCurrentFragment() as MapFragment).showEditOptions()
        }
        currScreen = nextScreen
        Log.d("[MYLOG]", "openScreen $nextScreen")
        return true
    }

    private fun getCurrentFragment(): Fragment? {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                return fragment
            }
        }
        return null
    }

    interface FabListener {
        fun onFabClick() {}
        fun onActionConfirm() {}
        fun onCancelAction() {}
    }
}
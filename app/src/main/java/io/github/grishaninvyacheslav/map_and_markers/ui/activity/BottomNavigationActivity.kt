package io.github.grishaninvyacheslav.map_and_markers.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
                bottomAppBar.visibility =
                    View.INVISIBLE // workaround бага https://github.com/material-components/material-components-android/issues/1361
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

    override fun navigateTo(screens: Screens) {
        openScreen(screens)
        binding.bottomNavigationView.menu.findItem(screens.menuId).isChecked = true
    }

    private lateinit var currScreen: Screens
    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currScreen = savedInstanceState?.getParcelable("currScreen") ?: Screens.Map
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        initBottomAppBar()
        setContentView(binding.root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("currScreen", currScreen)
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.screen_map -> openScreen(Screens.Map)
        R.id.screen_markers -> openScreen(Screens.Markers)
        else -> false
    }

    private fun openScreen(nextScreen: Screens): Boolean {
        val currentFragment: Fragment? = getCurrentFragmentOrNull()
        val fragmentToShow = supportFragmentManager.findFragmentByTag(nextScreen.menuId.toString())
        if (currentFragment != null && fragmentToShow != null && currentFragment === fragmentToShow) return false
        val transaction = supportFragmentManager.beginTransaction().apply {
            currentFragment?.let { setTransactionAnimation(this, currentFragment, nextScreen) }
        }
        if (fragmentToShow == null) {
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
        if (fragmentToShow != null) {
            transaction.show(fragmentToShow)
        }
        transaction.commitNow()
        if (nextScreen is Screens.MarkerCreator) {
            (getCurrentFragmentOrNull() as MapFragment).showEditOptions()
        }
        currScreen = nextScreen
        return true
    }

    private fun setTransactionAnimation(transaction: FragmentTransaction, currentFragment: Fragment, nextScreen: Screens){
        if (currentFragment is MapFragment && nextScreen == Screens.Markers) {
            transaction.setCustomAnimations(
                R.anim.slide_in_to_left,
                R.anim.slide_out_to_left,
                R.anim.slide_in_to_left,
                R.anim.slide_out_to_left
            )
        } else if (currentFragment is MarkersFragment && (nextScreen == Screens.Map || nextScreen == Screens.MarkerCreator)) {
            transaction.setCustomAnimations(
                R.anim.slide_in_to_right,
                R.anim.slide_out_to_right,
                R.anim.slide_in_to_right,
                R.anim.slide_out_to_right
            )
        }
    }

    private fun initBottomAppBar() = with(binding) {
        cancel.isVisible = false
        confirm.isVisible = false
        bottomNavigationView.background = null
        bottomNavigationView.setOnItemSelectedListener(this@BottomNavigationActivity)
        bottomNavigationView.selectedItemId = currScreen.menuId
        fab.setOnClickListener {
            (getCurrentFragmentOrNull() as? FabListener)?.onFabClick()
        }
        confirm.setOnClickListener {
            (getCurrentFragmentOrNull() as? FabListener)?.onActionConfirm()
        }
        cancel.setOnClickListener {
            (getCurrentFragmentOrNull() as? FabListener)?.onCancelAction()
        }
    }

    private fun getCurrentFragmentOrNull(): Fragment? {
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
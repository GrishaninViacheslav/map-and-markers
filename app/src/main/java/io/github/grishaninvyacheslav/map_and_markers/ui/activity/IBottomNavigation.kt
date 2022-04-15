package io.github.grishaninvyacheslav.map_and_markers.ui.activity

interface IBottomNavigation {
    fun hideNavigation()
    fun showNavigation()
    fun navigateTo(screens: Screens)
}
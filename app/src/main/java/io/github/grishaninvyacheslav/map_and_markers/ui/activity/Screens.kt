package io.github.grishaninvyacheslav.map_and_markers.ui.activity

import android.os.Parcelable
import io.github.grishaninvyacheslav.map_and_markers.R
import kotlinx.android.parcel.Parcelize

sealed class Screens(val menuId: Int): Parcelable {
    @Parcelize
    object Map: Screens(R.id.screen_map)
    @Parcelize
    object MarkerCreator: Screens(R.id.screen_map)
    @Parcelize
    object Markers: Screens(R.id.screen_markers)
}
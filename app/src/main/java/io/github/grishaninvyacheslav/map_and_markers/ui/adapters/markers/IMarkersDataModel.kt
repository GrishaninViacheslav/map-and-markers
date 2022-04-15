package io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers

interface IMarkersDataModel {
    fun getCount(): Int
    fun bindView(view: IMarkerItemView)
}
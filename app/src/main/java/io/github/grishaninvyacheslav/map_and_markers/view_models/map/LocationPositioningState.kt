package io.github.grishaninvyacheslav.map_and_markers.view_models.map

sealed class LocationPositioningState {
    object Positioning: LocationPositioningState()
    object Unavailable: LocationPositioningState()
    object Steady: LocationPositioningState()
}
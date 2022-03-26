package io.github.grishaninvyacheslav.map_and_markers.view_models

sealed class LocationPositioningState {
    object Positioning: LocationPositioningState()
    object Unavailable: LocationPositioningState()
    object Steady: LocationPositioningState()
}
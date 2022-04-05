package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

fun Marker.toMarkerOptions() = MarkerOptions()
    .position(
        LatLng(
            latitude,
            longitude
        )
    )
    .title(title)

fun MarkerOptions.toMarkerEntity() =
    Marker(
        uid = UUID.randomUUID().toString(),
        title = title ?: "",
        latitude = position.latitude,
        longitude = position.longitude
    )

fun Pair<String, MarkerOptions>.toMarkerEntity() =
    Marker(
        uid = first,
        title = second.title ?: "",
        latitude = second.position.latitude,
        longitude = second.position.longitude
    )
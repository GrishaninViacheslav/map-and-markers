package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = false) val uid: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)
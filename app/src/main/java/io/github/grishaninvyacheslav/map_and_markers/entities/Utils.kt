package io.github.grishaninvyacheslav.map_and_markers.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getBitmapDescriptorFromVector(id: Int, context: Context): BitmapDescriptor {
    val vectorDrawable: Drawable = ContextCompat.getDrawable(context, id)!!
    val height = (48 * context.resources.displayMetrics.density).toInt()
    val width = (48 * context.resources.displayMetrics.density).toInt()
    vectorDrawable.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
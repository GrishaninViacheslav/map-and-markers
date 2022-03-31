package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.markers

import android.content.Context
import androidx.appcompat.app.AlertDialog
import io.github.grishaninvyacheslav.map_and_markers.R

class MarkerRemoveConfirmDialogFactory {
    fun create(context: Context, markerToRemoveName: String, onConfirmCallback: () -> Unit) = AlertDialog.Builder(context).apply {
        setTitle(R.string.remove_marker_title)
        setMessage(
            String.format(
                context.getString(R.string.remove_marker_text),
                markerToRemoveName
            )
        )
        setNegativeButton(
            R.string.cancel
        ) { dialog, _ ->
            dialog.dismiss()
        }
        setPositiveButton(
            R.string.remove_marker
        ) { dialog, id ->
            onConfirmCallback.invoke()
            dialog.dismiss()
        }
    }.create()
}
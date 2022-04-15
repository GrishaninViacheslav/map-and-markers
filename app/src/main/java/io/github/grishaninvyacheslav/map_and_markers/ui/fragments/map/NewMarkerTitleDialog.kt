package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.map

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import io.github.grishaninvyacheslav.map_and_markers.R

class NewMarkerTitleDialog(
    private val context: Context,
    private val onConfirmCallback: (markerName: String) -> Unit
) {
    fun show() = dialog.show()

    fun dismiss() = dialog.dismiss()

    fun isShowing() = dialog.isShowing

    val view by lazy { EditText(context) }

    private val dialog by lazy {
        context.let {
            AlertDialog.Builder(it)
        }.apply {
            setView(view)
            setTitle(R.string.marker_title)
            setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(
                R.string.confirm
            ) { dialog, id ->
                onConfirmCallback(view.text.toString())
                dialog.dismiss()
            }
        }.create()
    }
}
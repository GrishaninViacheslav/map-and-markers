package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.markers

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import io.github.grishaninvyacheslav.map_and_markers.R

class MarkerRenameDialogFactory(private val context: Context) {
    private lateinit var newMarkerTitleInputView: EditText
    fun create(oldName: String, onRenameCallback: (newName: String) -> Unit) =
        AlertDialog.Builder(context).apply {
            setView(EditText(context).apply {
                newMarkerTitleInputView = this
                setText(oldName)
            })
            setTitle(R.string.marker_title)
            setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(
                R.string.confirm
            ) { dialog, _ ->
                onRenameCallback.invoke(newMarkerTitleInputView.text.toString())
                dialog.dismiss()
            }
        }.create()
}
package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.markers

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.grishaninvyacheslav.map_and_markers.databinding.DialogEditOptionsBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers.IMarkerItemView

class EditOptionsDialog(context: Context, view: IMarkerItemView) {
    private val dialog: BottomSheetDialog = BottomSheetDialog(context).apply {
        setContentView(
            DialogEditOptionsBinding.inflate(layoutInflater).apply {
                renameMarker.setOnClickListener {
                    onRenameMarkerClick?.invoke(view)
                    dismiss()
                }
                removeMarker.setOnClickListener {
                    onRemoveMarkerClick?.invoke(view)
                    dismiss()
                }
            }.root
        )
        setBehaviorAsAlwaysExpanded(behavior)
    }

    var onRenameMarkerClick: ((view: IMarkerItemView) -> Unit)? = null
    var onRemoveMarkerClick: ((view: IMarkerItemView) -> Unit)? = null

    fun show(){
        dialog.show()
    }

    private fun setBehaviorAsAlwaysExpanded(behavior: BottomSheetBehavior<*>){
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }
}
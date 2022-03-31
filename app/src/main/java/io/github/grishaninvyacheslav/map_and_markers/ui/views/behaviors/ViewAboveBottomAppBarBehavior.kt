package io.github.grishaninvyacheslav.map_and_markers.ui.views.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomappbar.BottomAppBar

class ViewAboveBottomAppBarBehavior @JvmOverloads constructor(
    context: Context? = null, attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean =
        dependency is BottomAppBar

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.layoutParams?.height = parent.height - dependency.height
        child.requestLayout()
        return false
    }
}
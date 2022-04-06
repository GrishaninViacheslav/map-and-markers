package io.github.grishaninvyacheslav.map_and_markers.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.ViewProgressButtonBinding

class ProgressButton : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(binding.apply {
            attrs?.getAttributeValue("http://schemas.android.com/apk/res-auto", "icon")?.let {
                button.setIconResource(it.substring(1, it.length).toInt())
            }
        }.root)
    }

    private val binding =
        ViewProgressButtonBinding.inflate(LayoutInflater.from(context)).apply {
            button.setOnClickListener { performClick() }
        }

    fun showState(progressState: ProgressState) = when (progressState) {
        ProgressState.IN_PROGRESS -> showProgressState()
        ProgressState.UNAVAILABLE -> showUnavailableState()
        ProgressState.STEADY -> showSteadyState()
    }

    enum class ProgressState {
        IN_PROGRESS,
        UNAVAILABLE,
        STEADY
    }

    private fun showProgressState() = with(binding) {
        progressBar.visibility = VISIBLE
        button.setIconTintResource(R.color.white)
        button.setBackgroundColor(
            context.getColor(
                R.color.purple_500
            )
        )
    }

    private fun showUnavailableState() = with(binding) {
        progressBar.visibility = INVISIBLE
        button.setIconTintResource(R.color.black)
        button.setBackgroundColor(context.getColor(R.color.gray))
    }

    private fun showSteadyState() = with(binding) {
        progressBar.visibility = INVISIBLE
        button.setIconTintResource(R.color.white)
        button.setBackgroundColor(context.getColor(R.color.purple_500))
    }
}
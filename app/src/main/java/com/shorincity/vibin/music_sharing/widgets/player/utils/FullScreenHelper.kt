package com.shorincity.vibin.music_sharing.widgets.player.utils

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.shorincity.vibin.music_sharing.widgets.player.listeners.YouTubePlayerFullScreenListener

internal class FullScreenHelper(private val targetView: View) {

    var isFullScreen: Boolean = false
        private set

    private val fullScreenListeners = mutableSetOf<YouTubePlayerFullScreenListener>()

    fun enterFullScreen() {
        if (isFullScreen) return

        isFullScreen = true

        val viewParams = targetView.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        if (viewParams is ConstraintLayout.LayoutParams) {
            viewParams.setMargins(0, 0, 0, 0)
        }

        targetView.layoutParams = viewParams

        for (fullScreenListener in fullScreenListeners)
            fullScreenListener.onYouTubePlayerEnterFullScreen()
    }

    fun exitFullScreen() {
        if (!isFullScreen) return

        isFullScreen = false

        val viewParams = targetView.layoutParams
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        if (viewParams is ConstraintLayout.LayoutParams) {
            viewParams.setMargins(dpToPx(20), dpToPx(30), dpToPx(20), 0)
        }

        targetView.layoutParams = viewParams

        for (fullScreenListener in fullScreenListeners)
            fullScreenListener.onYouTubePlayerExitFullScreen()
    }

    private fun dpToPx(dps: Int): Int {
        val scale: Float = targetView.context.resources.getDisplayMetrics().density
        return (dps * scale + 0.5f).toInt()
    }

    fun toggleFullScreen() {
        if (isFullScreen) exitFullScreen()
        else enterFullScreen()
    }

    fun addFullScreenListener(fullScreenListener: YouTubePlayerFullScreenListener): Boolean {
        return fullScreenListeners.add(fullScreenListener)
    }

    fun removeFullScreenListener(fullScreenListener: YouTubePlayerFullScreenListener): Boolean {
        return fullScreenListeners.remove(fullScreenListener)
    }
}

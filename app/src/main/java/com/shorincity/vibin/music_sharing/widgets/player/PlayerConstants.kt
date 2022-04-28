package com.shorincity.vibin.music_sharing.widgets.player

class PlayerConstants {

    enum class PlayerState {
        UNKNOWN, UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, VIDEO_CUED
    }

    enum class PlaybackQuality {
        UNKNOWN, SMALL, MEDIUM, LARGE, HD720, HD1080, HIGH_RES, DEFAULT
    }

    enum class PlayerError {
        UNKNOWN, INVALID_PARAMETER_IN_REQUEST, HTML_5_PLAYER, VIDEO_NOT_FOUND, VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
    }

    enum class PlaybackRate {
        UNKNOWN, RATE_0_25, RATE_0_5, RATE_1, RATE_1_5, RATE_2
    }
}

fun PlayerConstants.PlaybackRate.toFloat(): Float {
    return when (this) {
        PlayerConstants.PlaybackRate.UNKNOWN -> 1f
        PlayerConstants.PlaybackRate.RATE_0_25 -> 0.25f
        PlayerConstants.PlaybackRate.RATE_0_5 -> 0.5f
        PlayerConstants.PlaybackRate.RATE_1 -> 1f
        PlayerConstants.PlaybackRate.RATE_1_5 -> 1.5f
        PlayerConstants.PlaybackRate.RATE_2 -> 2f
    }
}

fun PlayerConstants.PlaybackQuality.getValue(): String {
    return when (this) {
        PlayerConstants.PlaybackQuality.SMALL -> "small"
        PlayerConstants.PlaybackQuality.MEDIUM -> "medium"
        PlayerConstants.PlaybackQuality.LARGE -> "large"
        PlayerConstants.PlaybackQuality.HD720 -> "hd720"
        PlayerConstants.PlaybackQuality.HD1080 -> "hd1080"
        PlayerConstants.PlaybackQuality.HIGH_RES -> "highres"
        PlayerConstants.PlaybackQuality.DEFAULT -> "default"
        PlayerConstants.PlaybackQuality.UNKNOWN -> "small"
    }
}
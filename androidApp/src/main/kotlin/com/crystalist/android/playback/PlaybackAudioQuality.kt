package com.crystalist.android.playback

import com.metrolist.innertubex.extraction.AudioQuality
import com.crystalist.shared.settings.Settings

internal fun playbackAudioQuality(): AudioQuality =
    try {
        AudioQuality.valueOf(Settings.shared.audioQuality)
    } catch (_: IllegalArgumentException) {
        AudioQuality.AUTO
    }

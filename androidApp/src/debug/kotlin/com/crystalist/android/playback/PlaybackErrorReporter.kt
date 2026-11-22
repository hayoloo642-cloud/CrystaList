package com.crystalist.android.playback

import com.crystalist.shared.model.SongItem
import com.crystalist.shared.playback.PlaybackErrorContext
import com.crystalist.shared.youtube.playback.PlaybackResource

internal object PlaybackErrorReporter {
    fun setActivePlayback(
        song: SongItem,
        resource: PlaybackResource?,
    ) = Unit

    fun clearActivePlayback() = Unit

    fun report(
        error: Throwable,
        context: PlaybackErrorContext,
    ) = Unit
}

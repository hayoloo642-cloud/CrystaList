/*
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.desktop.state.playback

import com.metrolist.innertubex.extraction.ContentHints
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.isVideoSong
import com.crystalist.shared.youtube.extraction.models.toContentHints
import com.crystalist.shared.youtube.playback.PlaybackResource

internal suspend fun selectDesktopPlaybackResource(
    song: SongItem,
    showVideoViewer: Boolean,
    showVideoFullscreen: Boolean,
    synchronizedPlayback: Boolean,
    videoPlaybackEnabled: Boolean,
    forcedClientId: String?,
    showError: Boolean,
    storedAudio: suspend () -> PlaybackResource?,
    fetchByHints: suspend (ContentHints, Boolean) -> PlaybackResource?,
): PlaybackResource? {
    if (forcedClientId == null) storedAudio()?.let { return it }
    val wantVideo =
        shouldRequestDesktopVideo(
            showVideoViewer = showVideoViewer,
            showVideoFullscreen = showVideoFullscreen,
            isVideoSong = song.isVideoSong,
            synchronizedPlayback = synchronizedPlayback,
            videoPlaybackEnabled = videoPlaybackEnabled,
        )
    val hints = song.toContentHints().copy(wantVideo = wantVideo)
    val resource = fetchByHints(hints, showError && !wantVideo)
    if (resource == null && wantVideo) {
        return fetchByHints(hints.copy(wantVideo = false), showError)
    }
    return resource
}

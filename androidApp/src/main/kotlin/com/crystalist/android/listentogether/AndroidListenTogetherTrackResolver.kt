package com.crystalist.android.listentogether

import com.crystalist.shared.listentogether.ListenTogetherTrackResolver
import com.crystalist.shared.listentogether.toSongItem
import com.crystalist.shared.model.SongItem

internal fun androidListenTogetherTrackResolver(
    metadata: suspend (String) -> SongItem?,
    fallbackArtist: suspend () -> String,
): ListenTogetherTrackResolver =
    ListenTogetherTrackResolver { track ->
        metadata(track.id) ?: track.toSongItem(
            fallbackArtist = fallbackArtist(),
            omitNonPositiveDuration = true,
        )
    }

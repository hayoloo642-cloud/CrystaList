package com.crystalist.desktop.state.content

import com.crystalist.shared.db.entities.PlaylistEntity
import com.crystalist.shared.db.hasCompleteStoredSongs
import com.crystalist.shared.model.SongItem
import kotlinx.coroutines.Job

internal fun hasCompleteStoredPlaylist(
    playlist: PlaylistEntity,
    songs: List<SongItem>,
): Boolean = playlist.hasCompleteStoredSongs(songs)

internal suspend fun awaitCompletePlaylist(
    loadJob: Job?,
    hasMore: () -> Boolean,
): Boolean {
    if (hasMore()) loadJob?.join()
    return !hasMore()
}

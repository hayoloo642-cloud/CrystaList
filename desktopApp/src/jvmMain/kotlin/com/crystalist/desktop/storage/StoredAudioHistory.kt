package com.crystalist.desktop.storage

import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.entities.toSongItem
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.isPodcast
import com.crystalist.shared.utils.Logger
import kotlinx.coroutines.CancellationException

internal suspend fun storedAudioReportingSong(
    song: SongItem?,
    database: MusicDatabase?,
): SongItem? {
    if (song == null || song.isLocal || song.isUploaded || song.isPodcast || song.id.startsWith("local_")) return song
    return try {
        database?.dao()?.getSongById(song.id)?.toSongItem() ?: song
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Logger.w("StoredAudioHistory", "Could not read playback reporting metadata", error)
        null
    }
}

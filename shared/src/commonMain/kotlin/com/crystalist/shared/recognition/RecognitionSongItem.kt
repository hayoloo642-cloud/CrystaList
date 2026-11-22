package com.crystalist.shared.recognition

import com.crystalist.shared.model.AlbumTiny
import com.crystalist.shared.model.ArtistTiny
import com.crystalist.shared.model.SongItem

internal fun recognitionSongItem(
    youtubeVideoId: String?,
    title: String,
    artist: String,
    album: String?,
    coverArtUrl: String?,
    coverArtHqUrl: String?,
    genre: String?,
): SongItem? =
    youtubeVideoId?.takeIf { it.isNotBlank() }?.let { videoId ->
        SongItem(
            id = videoId,
            title = title,
            thumbnail = coverArtHqUrl ?: coverArtUrl,
            artists = listOf(ArtistTiny(id = null, name = artist)),
            album = album?.let { AlbumTiny(id = null, name = it) },
            genre = genre,
        )
    }

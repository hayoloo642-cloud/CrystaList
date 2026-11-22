package com.crystalist.shared.ui.menu

import com.crystalist.shared.api.playlistSongs
import com.crystalist.shared.model.AlbumItem
import com.crystalist.shared.model.ArtistItem
import com.crystalist.shared.model.EpisodeItem
import com.crystalist.shared.model.PlaylistItem
import com.crystalist.shared.model.PodcastItem
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.YTItem
import com.crystalist.shared.model.toSongItem
import com.crystalist.shared.state.GlobalYouTubeRepository

suspend fun loadMediaItemSongs(item: YTItem): List<SongItem> {
    val repository = GlobalYouTubeRepository.instance
    return when (item) {
        is SongItem -> {
            listOf(item)
        }

        is EpisodeItem -> {
            listOf(item.toSongItem())
        }

        is AlbumItem -> {
            repository.getAlbum(item.id)?.songs.orEmpty()
        }

        is ArtistItem -> {
            repository.getArtistSongs(item.id)
        }

        is PlaylistItem -> {
            repository.getPlaylist(item.id).playlistSongs()
        }

        is PodcastItem -> {
            // Every page, so playback covers the whole podcast and can start from its first episode.
            buildList {
                repository.getPodcastPages(item.id).collect { page -> page.episodes.mapTo(this) { it.toSongItem(page.podcast.title) } }
            }
        }

        else -> {
            emptyList()
        }
    }
}

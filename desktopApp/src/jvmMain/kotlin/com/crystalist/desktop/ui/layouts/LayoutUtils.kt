/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.desktop.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.crystalist.desktop.state.app.UiState
import com.crystalist.desktop.state.content.ArtistState
import com.crystalist.desktop.state.content.BrowseState
import com.crystalist.desktop.state.content.HomeState
import com.crystalist.desktop.state.content.LibraryContentState
import com.crystalist.desktop.state.content.LibraryState
import com.crystalist.desktop.state.content.PlaylistAlbumState
import com.crystalist.desktop.state.content.PodcastState
import com.crystalist.desktop.state.content.SearchState
import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.app_name
import com.crystalist.shared.generated.resources.browse
import com.crystalist.shared.generated.resources.cached_playlist
import com.crystalist.shared.generated.resources.downloaded
import com.crystalist.shared.generated.resources.filter_library
import com.crystalist.shared.generated.resources.history
import com.crystalist.shared.generated.resources.home
import com.crystalist.shared.generated.resources.listen_together
import com.crystalist.shared.generated.resources.podcast
import com.crystalist.shared.generated.resources.statistics
import com.crystalist.shared.generated.resources.top_playlist_title
import com.crystalist.shared.generated.resources.uploaded_playlist_name
import com.crystalist.shared.settings.NavItem
import com.crystalist.shared.settings.NavigationId
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.ui.navigation.Screen
import org.jetbrains.compose.resources.stringResource

object LayoutUtils {
    var contentRefreshKey by mutableIntStateOf(0)
        private set

    fun refreshCurrentPage(route: Screen) {
        when (route) {
            is Screen.Artist -> {
                ArtistState.fetchArtistData(route.id, addToHistory = false)
            }

            is Screen.Playlist -> {
                PlaylistAlbumState.fetchPlaylistData(route.id, route.thumbnailHint, addToHistory = false)
            }

            is Screen.Album -> {
                PlaylistAlbumState.fetchAlbumData(route.id, addToHistory = false)
            }

            is Screen.Browse -> {
                BrowseState.fetchBrowseData(route.browseId, route.params, route.title, addToHistory = false)
            }

            is Screen.Podcast -> {
                PodcastState.fetch(route.id, addToHistory = false)
            }

            Screen.Search -> {
                SearchState.selectedSearchFilter?.let(SearchState::updateSearchFilter)
                    ?: SearchState.currentSearchQuery.takeIf(String::isNotBlank)?.let(SearchState::search)
            }

            Screen.Home -> {
                HomeState.refresh()
            }

            Screen.Library -> {
                LibraryContentState.fetchLibraryData(force = true)
            }

            Screen.History -> {
                contentRefreshKey++
            }

            else -> {
                contentRefreshKey++
            }
        }
    }

    @Composable
    fun rememberVisibleNavItems(): State<List<NavItem>> =
        remember {
            derivedStateOf {
                LibraryState.sidebarNavItems.filter {
                    it.visible && (it.id != NavigationId.LISTEN_TOGETHER || Settings.shared.listenTogetherEnabled)
                }
            }
        }

    @Composable
    fun rememberCurrentTitle(
        route: Screen,
        visibleItems: List<NavItem>,
        selectedNavIndex: Int,
    ): State<String> {
        val appName = stringResource(Res.string.app_name)
        val browse = stringResource(Res.string.browse)
        val podcast = stringResource(Res.string.podcast)
        val navigationTitle =
            when (visibleItems.getOrNull(selectedNavIndex)?.id) {
                NavigationId.HOME -> stringResource(Res.string.home)
                NavigationId.LIBRARY -> stringResource(Res.string.filter_library)
                NavigationId.HISTORY -> stringResource(Res.string.history)
                NavigationId.STATISTICS -> stringResource(Res.string.statistics)
                NavigationId.LISTEN_TOGETHER -> stringResource(Res.string.listen_together)
                else -> appName
            }
        val autoPlaylistTitle =
            when ((route as? Screen.AutoPlaylist)?.type) {
                LayoutConstants.PlaylistTypes.TOP100 -> stringResource(Res.string.top_playlist_title)
                LayoutConstants.PlaylistTypes.DOWNLOADED -> stringResource(Res.string.downloaded)
                LayoutConstants.PlaylistTypes.CACHED -> stringResource(Res.string.cached_playlist)
                LayoutConstants.PlaylistTypes.UPLOADED -> stringResource(Res.string.uploaded_playlist_name)
                else -> null
            }
        return remember(
            route,
            PodcastState.page,
            BrowseState.selectedBrowseTitle,
            navigationTitle,
            appName,
            browse,
            podcast,
            autoPlaylistTitle,
        ) {
            derivedStateOf {
                when (route) {
                    is Screen.Podcast -> PodcastState.page?.podcast?.title ?: podcast
                    is Screen.Browse -> BrowseState.selectedBrowseTitle ?: route.title ?: browse
                    is Screen.HomeSection -> route.title
                    is Screen.AutoPlaylist -> autoPlaylistTitle ?: navigationTitle
                    else -> navigationTitle
                }
            }
        }
    }

    fun syncContentState(route: Screen) {
        PlayerState.exitExpanded()
        UiState.showSignIn = false
        UiState.showSwitchChannel = false
        UiState.showSettings = false

        when (route) {
            is Screen.Artist -> {
                if (ArtistState.loadedArtistId == route.id) {
                    ArtistState.selectedArtistId = route.id
                } else if (ArtistState.selectedArtistId != route.id) {
                    ArtistState.fetchArtistData(route.id, addToHistory = false)
                }
            }

            is Screen.AboutArtist -> {
                if (ArtistState.loadedArtistId == route.id) {
                    ArtistState.selectedArtistId = route.id
                } else if (ArtistState.selectedArtistId != route.id) {
                    ArtistState.fetchArtistData(route.id, addToHistory = false)
                }
            }

            is Screen.Playlist -> {
                if (PlaylistAlbumState.selectedPlaylistId != route.id) {
                    PlaylistAlbumState.fetchPlaylistData(route.id, route.thumbnailHint, addToHistory = false)
                }
            }

            is Screen.Album -> {
                if (!PlaylistAlbumState.restoreAlbum(route.id) && PlaylistAlbumState.selectedAlbumId != route.id) {
                    PlaylistAlbumState.fetchAlbumData(route.id, addToHistory = false)
                }
            }

            is Screen.Browse -> {
                if (
                    BrowseState.selectedBrowseId != route.browseId ||
                    BrowseState.selectedBrowseParams != route.params
                ) {
                    BrowseState.fetchBrowseData(route.browseId, route.params, route.title, addToHistory = false)
                }
            }

            is Screen.Podcast -> {
                if (PodcastState.selectedPodcastId != route.id) PodcastState.fetch(route.id, addToHistory = false)
            }

            is Screen.AutoPlaylist -> {
                clearContentSelection(clearSearch = false)
                PlaylistAlbumState.selectedLocalPlaylist = route.type
            }

            Screen.Search -> {
                clearContentSelection(clearSearch = false)
            }

            Screen.Home,
            Screen.Library,
            Screen.History,
            Screen.Statistics,
            Screen.ListenTogether,
            -> {
                clearContentSelection(clearSearch = true)
            }

            else -> {
                return
            }
        }
    }

    private fun clearContentSelection(clearSearch: Boolean) {
        PlaylistAlbumState.selectedLocalPlaylist = null
        ArtistState.selectedArtistId = null
        PlaylistAlbumState.selectedPlaylistId = null
        PlaylistAlbumState.selectedPlaylistIdSetter = null
        PlaylistAlbumState.selectedAlbumId = null
        PlaylistAlbumState.selectedAlbumIdSetter = null
        BrowseState.clear()
        PodcastState.clear()
        if (clearSearch) SearchState.reset()
    }
}

package com.crystalist.desktop.state.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.storage.DownloadManager
import com.crystalist.desktop.storage.DownloadState
import com.crystalist.shared.auth.YouTubeSyncState
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.entities.PlaylistEntity
import com.crystalist.shared.db.preserveKnownPlaylistEditability
import com.crystalist.shared.model.PlaylistItem
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.UserPlaylist
import com.crystalist.shared.model.YTItem
import com.crystalist.shared.playlist.LibraryDeletionTracker
import com.crystalist.shared.playlist.createStoredLocalPlaylist
import com.crystalist.shared.playlist.deleteStoredPlaylist
import com.crystalist.shared.playlist.renameStoredPlaylist
import com.crystalist.shared.settings.BlockedContent
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.viewmodels.LibraryViewModel
import com.crystalist.shared.youtube.logging.YtLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.context.GlobalContext
import kotlin.time.Duration.Companion.seconds

private val CREATE_PLAYLIST_TIMEOUT = 60.seconds

internal class UserPlaylistSourceCache {
    private var localPlaylists: List<PlaylistEntity> = emptyList()
    private var remotePlaylists: List<PlaylistItem> = emptyList()
    private var remoteAccount = ""

    @Synchronized
    fun updateLocal(playlists: List<PlaylistEntity>) {
        localPlaylists = playlists
    }

    @Synchronized
    fun updateRemote(
        playlists: List<PlaylistItem>,
        account: String,
    ) {
        remotePlaylists = playlists
        remoteAccount = account
    }

    @Synchronized
    fun snapshot(account: String): Pair<List<PlaylistEntity>, List<PlaylistItem>> =
        localPlaylists to remotePlaylists.takeIf { remoteAccount == account }.orEmpty()
}

object LibraryContentState {
    private val database: MusicDatabase by lazy { GlobalContext.get().get() }
    private val libraryViewModel =
        LibraryViewModel(
            AppState.scope,
            database,
            DownloadManager.downloadStates.map { states -> states.filterValues { it == DownloadState.DOWNLOADED }.keys },
            downloadedSongDates =
                combine(DownloadManager.downloadedFiles, DownloadManager.downloadStates) { files, states ->
                    files.filter { states[it.songId] == DownloadState.DOWNLOADED }.associate { it.songId to it.downloadDate }
                },
        )
    var librarySections by mutableStateOf<Map<String, List<YTItem>>>(emptyMap())
        private set
    var userPlaylists by mutableStateOf<List<UserPlaylist>>(emptyList())
        private set
    var libraryError by mutableStateOf(false)
        private set
    var isLibraryLoading by mutableStateOf(true)
        private set
    var contentMatchedSongs by mutableStateOf<List<SongItem>>(emptyList())
        private set
    private var unfilteredLibraryItems: List<YTItem> = emptyList()
    private val playlistSources = UserPlaylistSourceCache()
    private val playlistRenameRequests = PlaylistRenameRequests()
    private var playlistObservationJob: Job? = null
    private var playlistRefreshJob: Job? = null
    private var playlistRefreshGeneration = 0L

    init {
        refreshUserPlaylists()
        AppState.scope.launch {
            libraryViewModel.uiState.collect { state ->
                unfilteredLibraryItems = state.items
                librarySections = mapOf("Library" to BlockedContent.filterItems(unfilteredLibraryItems))
                isLibraryLoading = state.isLoading || state.isRefreshing
                libraryError = state.error
            }
        }
        AppState.scope.launch {
            BlockedContent.version.drop(1).collect { applyBlockedContentFilter() }
        }
        AppState.scope.launch {
            libraryViewModel.contentMatchedSongs.collect { contentMatchedSongs = it }
        }
    }

    val contentSearchHasMore = libraryViewModel.contentSearchHasMore
    val presentation = libraryViewModel.presentation

    fun setActive(active: Boolean) {
        libraryViewModel.setActive(active)
        if (!active) {
            playlistObservationJob?.cancel()
            playlistObservationJob = null
        } else if (playlistObservationJob == null) {
            playlistObservationJob =
                AppState.scope.launch {
                    database.dao().allPlaylists().collect { playlists ->
                        playlistSources.updateLocal(playlists)
                        publishUserPlaylists()
                    }
                }
        }
    }

    fun loadMoreContentMatches() = libraryViewModel.loadMoreContentMatches()

    fun setSearchDescending(descending: Boolean) = libraryViewModel.setSearchDescending(descending)

    fun updateLibrarySearchQuery(query: String) {
        libraryViewModel.setSearchQuery(query)
    }

    fun fetchLibraryData(force: Boolean = false) {
        libraryViewModel.refresh(force)
        refreshUserPlaylists()
    }

    fun createPlaylist(
        name: String,
        onResult: (Result<String>) -> Unit,
    ) {
        AppState.scope.launch {
            val local = !GlobalYouTubeRepository.instance.canManagePlaylists()
            val result =
                withTimeoutOrNull(CREATE_PLAYLIST_TIMEOUT) {
                    withContext(Dispatchers.IO) {
                        if (local) {
                            createStoredLocalPlaylist(database, name).map { it.id }
                        } else {
                            GlobalYouTubeRepository.instance.createPlaylist(name)
                        }
                    }
                } ?: Result.failure(IllegalStateException("Creating the playlist timed out"))
            result.onSuccess { playlistId ->
                if (!local) {
                    withContext(Dispatchers.IO) {
                        database.dao().insert(
                            PlaylistEntity(
                                name = name.trim(),
                                browseId = playlistId.removePrefix("VL"),
                                isEditable = true,
                                isAutoSync = true,
                            ),
                        )
                    }
                }
                fetchLibraryData(force = true)
            }
            onResult(result)
        }
    }

    fun renamePlaylist(
        playlist: PlaylistItem,
        name: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        AppState.scope.launch {
            val result =
                playlistRenameRequests.save(playlist.id) {
                    withContext(Dispatchers.IO) {
                        renameStoredPlaylist(database, GlobalYouTubeRepository.instance, playlist.id, name)
                    }
                }
            onResult(result)
            if (result.isSuccess) fetchLibraryData(force = true)
        }
    }

    fun deletePlaylist(
        playlist: PlaylistItem,
        onResult: (Result<Unit>) -> Unit,
    ) = AppState.scope.launch {
        val result =
            withContext(Dispatchers.IO) {
                deleteStoredPlaylist(database, GlobalYouTubeRepository.instance, playlist.id)
            }
        if (result.isSuccess) {
            libraryViewModel.onPlaylistDeleted(playlist.id)
            val normalizedId = playlist.id.removePrefix("VL")
            userPlaylists = userPlaylists.filterNot { it.id.removePrefix("VL") == normalizedId }
            fetchLibraryData(force = true)
        }
        onResult(result)
    }

    private fun applyBlockedContentFilter() {
        librarySections = mapOf("Library" to BlockedContent.filterItems(unfilteredLibraryItems))
    }

    private fun refreshUserPlaylists() {
        val generation = ++playlistRefreshGeneration
        val accountKey = currentPlaylistAccountKey()
        playlistRefreshJob?.cancel()
        playlistRefreshJob =
            AppState.scope.launch {
                val remotePlaylists =
                    try {
                        if (GlobalYouTubeRepository.instance.canManagePlaylists()) {
                            val result = GlobalYouTubeRepository.instance.getUserPlaylistsResult()
                            if (!result.complete) {
                                YouTubeSyncState.failed("playlists") { fetchLibraryData(force = true) }
                                return@launch
                            }
                            YouTubeSyncState.succeeded("playlists")
                            database
                                .preserveKnownPlaylistEditability(result.items, result.unresolvedPlaylistEditabilityIds)
                                .filterIsInstance<PlaylistItem>()
                        } else {
                            emptyList()
                        }
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        YtLogger.e("LibrarySync", "Playlist picker failed type=${error::class.simpleName}")
                        YouTubeSyncState.failed("playlists") { fetchLibraryData(force = true) }
                        return@launch
                    }
                if (generation != playlistRefreshGeneration || accountKey != currentPlaylistAccountKey()) return@launch
                playlistSources.updateRemote(remotePlaylists, accountKey)
                publishUserPlaylists()
            }
    }

    private fun publishUserPlaylists() {
        val (localPlaylists, cachedRemotePlaylists) = playlistSources.snapshot(currentPlaylistAccountKey())
        val remotePlaylists =
            cachedRemotePlaylists.filterNot { LibraryDeletionTracker.isDeleted("playlist:${it.id.removePrefix("VL")}") }
        val playlists = mutableListOf<UserPlaylist>()
        val likedPlaylist = remotePlaylists.firstOrNull { it.isLikedMusicPlaylist() }
        likedPlaylist?.let { playlists += it.toUserPlaylist(isAutoPlaylist = true) }
        remotePlaylists.filterNot { it.isLikedMusicPlaylist() }.forEach { playlist ->
            if (playlists.none { it.id.removePrefix("VL") == playlist.id.removePrefix("VL") }) {
                playlists += playlist.toUserPlaylist(isAutoPlaylist = false)
            }
        }
        localPlaylists
            .filter { it.id != "LM" && it.browseId != "LM" }
            .forEach { playlist ->
                val id = playlist.browseId ?: playlist.id
                if (playlists.none { it.id.removePrefix("VL") == id.removePrefix("VL") }) {
                    playlists +=
                        UserPlaylist(
                            id = playlist.browseId?.let { if (it.startsWith("VL")) it else "VL$it" } ?: playlist.id,
                            name = playlist.name,
                            thumbnailUrl = playlist.customThumbnailUrl ?: playlist.thumbnailUrl,
                            songCount = playlist.remoteSongCount,
                            isAutoPlaylist = playlist.remoteSongCount == null,
                        )
                }
            }
        userPlaylists = playlists
    }

    private fun currentPlaylistAccountKey(): String =
        Settings.shared.dataSyncId.ifBlank {
            Settings.shared.accountEmail.ifBlank { "signed-out" }
        } + "|${Settings.shared.innerTubeAuthUser}"

    private fun PlaylistItem.isLikedMusicPlaylist(): Boolean =
        id.equals("LM", ignoreCase = true) ||
            id.equals("VLLM", ignoreCase = true) ||
            id.startsWith("LM:")

    private fun PlaylistItem.toUserPlaylist(isAutoPlaylist: Boolean): UserPlaylist =
        UserPlaylist(id, title, thumbnail, null, author, isAutoPlaylist)
}

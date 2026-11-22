/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.crystalist.desktop.ui.components
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.state.content.ArtistState
import com.crystalist.desktop.state.content.HomeState
import com.crystalist.desktop.state.content.LibraryState
import com.crystalist.desktop.state.content.PlaylistAlbumState
import com.crystalist.desktop.state.content.PodcastState
import com.crystalist.desktop.state.playback.ListenTogetherState
import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.desktop.storage.CachedSongEntry
import com.crystalist.desktop.storage.DownloadManager
import com.crystalist.desktop.storage.DownloadState
import com.crystalist.desktop.storage.SongCacheManager
import com.crystalist.desktop.storage.playlistDownloadProgress
import com.crystalist.desktop.storage.playlistDownloadState
import com.crystalist.desktop.utils.exportCollection
import com.crystalist.shared.api.playlistSongs
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.collectionPlayCounts
import com.crystalist.shared.db.loadAndCachePlaylist
import com.crystalist.shared.db.refreshHistoryPlaylist
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.action_download
import com.crystalist.shared.generated.resources.action_like
import com.crystalist.shared.generated.resources.add_to_playlist
import com.crystalist.shared.generated.resources.add_to_queue
import com.crystalist.shared.generated.resources.cancel
import com.crystalist.shared.generated.resources.delete_downloaded_audio
import com.crystalist.shared.generated.resources.delete_playlist
import com.crystalist.shared.generated.resources.dislike_song
import com.crystalist.shared.generated.resources.downloading_percent
import com.crystalist.shared.generated.resources.error_unknown
import com.crystalist.shared.generated.resources.export_collection
import com.crystalist.shared.generated.resources.export_format_csv
import com.crystalist.shared.generated.resources.export_format_csv_description
import com.crystalist.shared.generated.resources.export_format_m3u
import com.crystalist.shared.generated.resources.export_format_m3u_description
import com.crystalist.shared.generated.resources.hide_from_library
import com.crystalist.shared.generated.resources.make_available_offline
import com.crystalist.shared.generated.resources.move_down
import com.crystalist.shared.generated.resources.move_up
import com.crystalist.shared.generated.resources.percent_symbol
import com.crystalist.shared.generated.resources.pin_to_speed_dial
import com.crystalist.shared.generated.resources.play
import com.crystalist.shared.generated.resources.play_next
import com.crystalist.shared.generated.resources.remove
import com.crystalist.shared.generated.resources.remove_dislike
import com.crystalist.shared.generated.resources.remove_download
import com.crystalist.shared.generated.resources.remove_from_playlist
import com.crystalist.shared.generated.resources.remove_from_saved_episodes
import com.crystalist.shared.generated.resources.remove_from_saved_podcasts
import com.crystalist.shared.generated.resources.rename
import com.crystalist.shared.generated.resources.save
import com.crystalist.shared.generated.resources.save_episode
import com.crystalist.shared.generated.resources.save_podcast
import com.crystalist.shared.generated.resources.select_songs
import com.crystalist.shared.generated.resources.share
import com.crystalist.shared.generated.resources.shuffle
import com.crystalist.shared.generated.resources.start_radio
import com.crystalist.shared.generated.resources.stored_remove_selected_message
import com.crystalist.shared.generated.resources.stored_remove_selected_title
import com.crystalist.shared.generated.resources.subscribe
import com.crystalist.shared.generated.resources.suggest_to_host
import com.crystalist.shared.generated.resources.unlike
import com.crystalist.shared.generated.resources.unpin_from_speed_dial
import com.crystalist.shared.generated.resources.unsubscribe
import com.crystalist.shared.generated.resources.view_album
import com.crystalist.shared.generated.resources.view_artist
import com.crystalist.shared.generated.resources.view_podcast
import com.crystalist.shared.model.AlbumItem
import com.crystalist.shared.model.ArtistItem
import com.crystalist.shared.model.ArtistTiny
import com.crystalist.shared.model.BuiltinPlaylist
import com.crystalist.shared.model.CollectionSort
import com.crystalist.shared.model.EpisodeItem
import com.crystalist.shared.model.PlaylistItem
import com.crystalist.shared.model.PodcastItem
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.YTItem
import com.crystalist.shared.model.presentCollectionWithPlayCounts
import com.crystalist.shared.model.previewThumbnailUrl
import com.crystalist.shared.model.speedDialItemId
import com.crystalist.shared.model.toSongItem
import com.crystalist.shared.settings.BlockedContent
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.state.SongLikeState
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.Material3MenuGroup
import com.crystalist.shared.ui.components.Material3MenuItemData
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.components.PopupContent
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.components.images.AsyncImage
import com.crystalist.shared.ui.components.localizedBuiltinPlaylist
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.menu.CursorContextMenuState
import com.crystalist.shared.ui.menu.CursorDropdownMenu
import com.crystalist.shared.ui.menu.ExpressiveMenuActionGroup
import com.crystalist.shared.ui.menu.MediaContextMenu
import com.crystalist.shared.ui.menu.MediaDownloadState
import com.crystalist.shared.ui.menu.MediaMenuModel
import com.crystalist.shared.ui.menu.MediaMenuPlaybackActions
import com.crystalist.shared.ui.menu.albumMediaMenuModel
import com.crystalist.shared.ui.menu.artistMediaMenuModel
import com.crystalist.shared.ui.menu.cursorContextMenu
import com.crystalist.shared.ui.menu.loadMediaItemSongs
import com.crystalist.shared.ui.menu.playlistMediaMenuModel
import com.crystalist.shared.ui.menu.podcastMediaMenuModel
import com.crystalist.shared.ui.menu.rememberCursorContextMenuState
import com.crystalist.shared.ui.menu.rememberDeleteUploadAction
import com.crystalist.shared.ui.menu.songMediaMenuModel
import com.crystalist.shared.ui.playback.LocalPlaybackController
import com.crystalist.shared.ui.playback.PlaybackController
import com.crystalist.shared.ui.screens.PendingCollectionPlayState
import com.crystalist.shared.ui.theme.ComponentShapes
import com.crystalist.shared.utils.CollectionExportFormat
import com.crystalist.shared.utils.ShareUrls
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext
import com.crystalist.shared.db.entities.toSongItem as localSongItem

private val sharedMenuPlayState = PendingCollectionPlayState()
private val sharedMenuQueueAdditions = MenuQueueAdditionCoordinator()

private class MenuQueueAdditionCoordinator {
    private val lock = Any()
    private var tail: Job? = null
    private var activeReplacementGeneration: Long? = null

    fun cancel() {
        synchronized(lock) {
            activeReplacementGeneration = null
            tail?.cancel()
            tail = null
        }
    }

    fun enqueue(
        item: YTItem,
        existingSongs: List<SongItem>,
        playback: PlaybackController,
        invoke: MediaMenuPlaybackActions.() -> Unit,
    ) {
        val expectedReplacementGeneration = playback.reserveQueueAdditionGeneration()
        synchronized(lock) {
            if (activeReplacementGeneration != expectedReplacementGeneration) {
                tail?.cancel()
                tail = null
                activeReplacementGeneration = expectedReplacementGeneration
            }
            val previous = tail
            val job =
                AppState.scope.launch {
                    previous?.join()
                    if (playback.playbackReplacementGeneration != expectedReplacementGeneration) return@launch
                    val loadedSongs =
                        try {
                            loadMediaSongs(item, existingSongs)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            AppState.scope.launch {
                                AppState.snackbarHostState.showSnackbar(getString(Res.string.error_unknown))
                            }
                            return@launch
                        }
                    if (loadedSongs.isEmpty() ||
                        playback.playbackReplacementGeneration != expectedReplacementGeneration
                    ) {
                        return@launch
                    }
                    val guardedPlayback = ReservedPlaybackController(playback, expectedReplacementGeneration)
                    MediaMenuPlaybackActions(
                        item,
                        guardedPlayback,
                        withSongs = { action -> action(loadedSongs) },
                    ).invoke()
                }
            tail = job
            job.invokeOnCompletion {
                synchronized(lock) {
                    if (tail === job) tail = null
                }
            }
        }
    }
}

@Composable
fun SongContextMenu(
    song: SongItem,
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    removeLabel: String? = null,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onHideFromLibrary: (() -> Unit)? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
    onPlaybackAction: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    var showDetails by remember(song.id) { mutableStateOf(false) }
    var showEditDialog by remember(song.id) { mutableStateOf(false) }
    val deleteUploadAction =
        if (expanded) rememberDeleteUploadAction(song, remember { GlobalContext.get().get<MusicDatabase>() }, onDismiss) else null
    if (showEditDialog) {
        SongMetadataEditorDialog(
            song = song,
            onSaved = {
                showEditDialog = false
                onDismiss()
            },
            onDismiss = { showEditDialog = false },
        )
    }
    PopupVisibility(visible = showDetails) {
        MediaDetailsDialog(song, onDismiss = {
            showDetails = false
            onDismiss()
        })
    }
    MediaContextMenu(expanded = expanded && !showEditDialog && !showDetails, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val isLiked = PlayerState.isLiked(song.id)
        val isDisliked = PlayerState.isDisliked(song.id)
        val isGuest = PlayerState.isListenTogetherGuest()
        val downloadState = mediaDownloadState(song.id)
        val isPinned = song.id in HomeState.uiState.speedDialPinnedIds
        songMediaMenuModel(
            song = song,
            deleteUploadAction = deleteUploadAction,
            onDismiss = onDismiss,
            isLiked = isLiked,
            isDisliked = isDisliked,
            isGuest = isGuest,
            isPinned = isPinned,
            downloadState = downloadState,
            downloadProgress = ((LibraryState.downloadProgress[song.id] ?: 0f) * 100).toInt(),
            onPlay = {
                sharedMenuPlayState.cancel()
                sharedMenuQueueAdditions.cancel()
                onPlaybackAction()
                playback.play(song)
            },
            onLike = {
                PlayerState.toggleLike(song)
                onDismiss()
            },
            onDislike = { PlayerState.toggleDislike(song) },
            onStartRadio = {
                sharedMenuPlayState.cancel()
                sharedMenuQueueAdditions.cancel()
                onPlaybackAction()
                playback.startRadio(song)
            },
            onAddToPlaylist = {
                showAddToPlaylist(song)
                onDismiss()
            },
            onShare = { shareSong(song) },
            onSuggestToHost = { suggestToHost(song) },
            onPlayNext = { sharedMenuQueueAdditions.enqueue(song, listOf(song), playback) { playNext() } },
            onAddToQueue = { sharedMenuQueueAdditions.enqueue(song, listOf(song), playback) { addToQueue() } },
            onTogglePin = {
                if (isPinned) HomeState.unpinSpeedDialItem(song.id) else HomeState.pinSpeedDialItem(song)
            },
            onDownload = {
                if (downloadState == MediaDownloadState.DOWNLOADED) {
                    DownloadManager.deleteDownload(song.id)
                } else {
                    DownloadManager.download(song)
                }
            },
            onBlockArtist =
                song.artists.firstOrNull { it.id != null }?.let { artist ->
                    { PlayerState.blockArtist(artist) }
                },
            onHideFromLibrary = onHideFromLibrary,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onRemove = onRemoveFromPlaylist,
            removeLabel = removeLabel ?: stringResource(Res.string.remove_from_playlist),
            onShowDetails = { showDetails = true },
            onEdit = { showEditDialog = true },
            onGoToArtist = ArtistState::fetchArtistData,
            onGoToAlbum =
                song.album?.id?.let { albumId ->
                    {
                        if (song.podcastId != null) {
                            PodcastState.fetch(albumId)
                        } else {
                            PlaylistAlbumState.fetchAlbumData(albumId)
                        }
                    }
                },
            additionalActions = additionalActions,
        )
    }
}

@Composable
fun AlbumContextMenu(
    album: AlbumItem,
    songs: List<SongItem> = emptyList(),
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    isFavorite: Boolean = false,
    onFavorite: (() -> Unit)? = null,
    onHideFromLibrary: (() -> Unit)? = null,
    pinnedId: String? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
) {
    MediaContextMenu(expanded = expanded, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val isPinned = album.id in HomeState.uiState.speedDialPinnedIds
        val playbackActions =
            MediaMenuPlaybackActions(
                album,
                playback,
                withSongs = { action -> withMediaSongs(album, songs, action) },
            )
        albumMediaMenuModel(
            album = album,
            isFavorite = isFavorite,
            onFavorite = onFavorite,
            onDismiss = onDismiss,
            isPinned = isPinned,
            onPlay = playbackActions::play,
            onShuffle = playbackActions::shuffle,
            onShare = { AppState.copyToClipboard(ShareUrls.album(album.id)) },
            onPlayNext = playbackActions::playNext,
            onAddToQueue = playbackActions::addToQueue,
            onAddToPlaylist = { withMediaSongs(album, songs) { PlayerState.showAddToPlaylistSongs = it } },
            onTogglePin = {
                if (isPinned) HomeState.unpinSpeedDialItem(album.id) else HomeState.pinSpeedDialItem(album)
            },
            onDownload = { withMediaSongs(album, songs) { loaded -> loaded.forEach(DownloadManager::download) } },
            onHideFromLibrary = onHideFromLibrary,
            onGoToArtist = ArtistState::fetchArtistData,
            additionalActions = additionalActions,
        )
    }
}

@Composable
fun ArtistContextMenu(
    artist: ArtistItem,
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    onHideFromLibrary: (() -> Unit)? = null,
    pinnedId: String? = null,
    isSubscribed: Boolean? = null,
    onToggleSubscription: (() -> Unit)? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
) {
    MediaContextMenu(expanded = expanded, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val isPinned = artist.id in HomeState.uiState.speedDialPinnedIds
        val isBlocked = BlockedContent.isArtistBlocked(artist.id)
        val playbackActions =
            MediaMenuPlaybackActions(
                artist,
                playback,
                withSongs = { action -> withMediaSongs(artist, emptyList(), action) },
            )
        artistMediaMenuModel(
            artist = artist,
            onDismiss = onDismiss,
            isPinned = isPinned,
            isBlocked = isBlocked,
            isSubscribed = isSubscribed,
            onPlay = playbackActions::play,
            onShuffle = playbackActions::shuffle,
            onShare = { AppState.copyToClipboard(ShareUrls.artist(artist.id)) },
            onPlayNext = playbackActions::playNext,
            onAddToQueue = playbackActions::addToQueue,
            onTogglePin = {
                if (isPinned) HomeState.unpinSpeedDialItem(artist.id) else HomeState.pinSpeedDialItem(artist)
            },
            onBlock = {
                if (isBlocked) {
                    BlockedContent.unblockArtist(artist.id)
                } else {
                    PlayerState.blockArtist(ArtistTiny(artist.id, artist.title, artist.thumbnail))
                }
            },
            onToggleSubscription = onToggleSubscription,
            onHideFromLibrary = onHideFromLibrary,
            additionalActions = additionalActions,
        )
    }
}

@Composable
fun PlaylistContextMenu(
    playlist: PlaylistItem,
    songs: List<SongItem> = emptyList(),
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    isFavorite: Boolean = false,
    onFavorite: (() -> Unit)? = null,
    onSelect: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onHideFromLibrary: (() -> Unit)? = null,
    hideFromLibraryLabel: String? = null,
    pinnedId: String? = null,
    fallbackIcon: ImageVector? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
    downloadSongs: List<SongItem> = songs,
    pendingPlayState: PendingCollectionPlayState? = null,
    onPlaybackAction: () -> Unit = {},
) {
    val playlist = localizedBuiltinPlaylist(playlist)
    var showExportDialog by remember(playlist.id) { mutableStateOf(false) }
    val removal = remember(playlist.id) { DownloadRemovalState() }
    val isDownloadedCollection = playlist.id == "auto:downloaded"
    val actionSongs =
        if (isDownloadedCollection && songs.isEmpty()) LibraryState.downloadedFiles.map { it.toSongItem() } else songs
    val downloadActionSongs =
        if (isDownloadedCollection && downloadSongs.isEmpty()) {
            LibraryState.downloadedFiles.map { it.toSongItem() }
        } else {
            downloadSongs.ifEmpty { actionSongs }
        }
    MediaContextMenu(expanded = expanded, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val isPinned = HomeState.uiState.speedDialPinnedIds.any { speedDialItemId(it) == speedDialItemId(playlist.id) }
        val builtin = BuiltinPlaylist.fromId(playlist.id)
        if (builtin in setOf(BuiltinPlaylist.HISTORY, BuiltinPlaylist.NEW_EPISODES, BuiltinPlaylist.SAVED_EPISODES)) {
            return@MediaContextMenu playlistMediaMenuModel(
                playlist = playlist,
                onDismiss = onDismiss,
                isPinned = isPinned,
                onTogglePin = {
                    if (isPinned) HomeState.unpinSpeedDialItem(playlist.id) else HomeState.pinSpeedDialItem(playlist)
                },
                additionalActions = additionalActions,
            )
        }
        val isAuto = playlist.id.startsWith("auto:")
        val isLocal = playlist.isLocal || isAuto
        val loadedSongs = downloadActionSongs.takeIf { it.isNotEmpty() }
        val loadedDownloadState = loadedSongs?.let { playlistDownloadState(it, LibraryState.downloadStates) }
        val downloadKey = "playlist:${playlist.id.removePrefix("VL")}"
        val downloadedKeys =
            if (isAuto) {
                emptyList()
            } else {
                val downloadedSongIds = LibraryState.downloadStates.filterValues { it == DownloadState.DOWNLOADED }.keys
                remember(downloadedSongIds) {
                    GlobalContext
                        .get()
                        .get<MusicDatabase>()
                        .dao()
                        .downloadedCollectionKeys(downloadedSongIds.toList())
                }.collectAsState(initial = if (playlist.isDownloaded) listOf(downloadKey) else emptyList()).value
            }
        val isDownloaded =
            if (isAuto) {
                loadedDownloadState == DownloadState.DOWNLOADED || (loadedSongs == null && playlist.isDownloaded)
            } else {
                downloadKey in downloadedKeys
            }
        val downloadState =
            when {
                isDownloaded -> MediaDownloadState.DOWNLOADED
                loadedDownloadState == DownloadState.DOWNLOADING -> MediaDownloadState.DOWNLOADING
                else -> MediaDownloadState.NOT_DOWNLOADED
            }
        val downloadProgress =
            loadedSongs?.let { playlistDownloadProgress(it, LibraryState.downloadStates, LibraryState.downloadProgress) } ?: 0
        playlistMediaMenuModel(
            playlist = playlist,
            isFavorite = isFavorite,
            onFavorite = onFavorite,
            onDismiss = onDismiss,
            isPinned = isPinned,
            fallbackIcon = fallbackIcon,
            downloadState = downloadState,
            downloadProgress = downloadProgress,
            onPlay = {
                launchTrackedMenuPlayback(
                    playlist,
                    actionSongs,
                    playback,
                    pendingPlayState = pendingPlayState,
                    onPlaybackAction = onPlaybackAction,
                    invoke = MediaMenuPlaybackActions::play,
                )
            },
            onShuffle = {
                launchTrackedMenuPlayback(
                    playlist,
                    actionSongs,
                    playback,
                    pendingPlayState = pendingPlayState,
                    onPlaybackAction = onPlaybackAction,
                    invoke = MediaMenuPlaybackActions::shuffle,
                )
            },
            onShare =
                if (isLocal) {
                    null
                } else {
                    { AppState.copyToClipboard(ShareUrls.playlist(playlist.id)) }
                },
            onPlayNext = {
                launchTrackedMenuQueueAction(playlist, actionSongs, playback, MediaMenuPlaybackActions::playNext)
            },
            onAddToQueue = {
                launchTrackedMenuQueueAction(playlist, actionSongs, playback, MediaMenuPlaybackActions::addToQueue)
            },
            onTogglePin = {
                if (isPinned) HomeState.unpinSpeedDialItem(playlist.id) else HomeState.pinSpeedDialItem(playlist)
            },
            onDownload = {
                withMediaSongs(playlist, downloadActionSongs) { playlistSongs ->
                    if (downloadState == MediaDownloadState.DOWNLOADED) {
                        if (isDownloadedCollection) {
                            removal.request(playlistSongs.map(SongItem::id))
                        } else {
                            DownloadManager.removePlaylistDownloads(playlist.id, playlistSongs)
                        }
                    } else {
                        DownloadManager.downloadPlaylist(playlist.id, playlistSongs)
                    }
                }
            },
            onHideFromLibrary = onHideFromLibrary,
            hideFromLibraryLabel = hideFromLibraryLabel,
            onExport = { showExportDialog = true },
            onSelect = onSelect,
            onRename = onRename,
            onDelete = onDelete,
            additionalActions = additionalActions,
        )
    }
    PopupContent(removal.pendingIds) { ids ->
        AlertDialog(
            onDismissRequest = removal::dismiss,
            title = { Text(pluralStringResource(Res.plurals.stored_remove_selected_title, ids.size, ids.size)) },
            text = { Text(stringResource(Res.string.stored_remove_selected_message)) },
            confirmButton = {
                TextButton(
                    onClick = { removal.confirm(DownloadManager::deleteDownloads) },
                    shapes = ButtonDefaults.shapes(),
                ) { Text(stringResource(Res.string.remove)) }
            },
            dismissButton = {
                TextButton(onClick = removal::dismiss, shapes = ButtonDefaults.shapes()) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
    if (showExportDialog) {
        var format by remember { mutableStateOf(CollectionExportFormat.CSV) }
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(stringResource(Res.string.export_collection, playlist.title)) },
            text = {
                Material3MenuGroup(
                    items =
                        CollectionExportFormat.entries.map { option ->
                            Material3MenuItemData(
                                title = {
                                    Text(
                                        stringResource(
                                            if (option ==
                                                CollectionExportFormat.CSV
                                            ) {
                                                Res.string.export_format_csv
                                            } else {
                                                Res.string.export_format_m3u
                                            },
                                        ),
                                    )
                                },
                                description = {
                                    Text(
                                        stringResource(
                                            if (option == CollectionExportFormat.CSV) {
                                                Res.string.export_format_csv_description
                                            } else {
                                                Res.string.export_format_m3u_description
                                            },
                                        ),
                                    )
                                },
                                trailingContent = { RadioButton(selected = format == option, onClick = null) },
                                onClick = { format = option },
                            )
                        },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExportDialog = false
                        exportCollection(playlist.title, format) { loadMediaSongs(playlist, actionSongs) }
                    },
                    shapes = ButtonDefaults.shapes(),
                ) { Text(stringResource(Res.string.save)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) { Text(stringResource(Res.string.cancel)) }
            },
        )
    }
}

@Composable
fun PodcastContextMenu(
    podcast: PodcastItem,
    songs: List<SongItem> = emptyList(),
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    onRemoveFromLibrary: (() -> Unit)? = null,
    pinnedId: String? = null,
    isSaved: Boolean? = null,
    onToggleSaved: (() -> Unit)? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
) {
    MediaContextMenu(expanded = expanded, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val isPinned = podcast.id in HomeState.uiState.speedDialPinnedIds
        val playbackActions =
            MediaMenuPlaybackActions(
                podcast,
                playback,
                withSongs = { action -> withMediaSongs(podcast, songs, action) },
            )
        podcastMediaMenuModel(
            podcast = podcast,
            onDismiss = onDismiss,
            isPinned = isPinned,
            isSaved = isSaved,
            onPlay = playbackActions::play,
            onShuffle = playbackActions::shuffle,
            onPlayOldestFirst = playbackActions::playOldestFirst,
            onShare = { AppState.copyToClipboard(ShareUrls.podcast(podcast.id)) },
            onPlayNext = playbackActions::playNext,
            onAddToQueue = playbackActions::addToQueue,
            onTogglePin = {
                if (isPinned) HomeState.unpinSpeedDialItem(podcast.id) else HomeState.pinSpeedDialItem(podcast)
            },
            onToggleSaved = onToggleSaved,
            onRemoveFromLibrary = onRemoveFromLibrary,
            onDownload = { withMediaSongs(podcast, songs) { loaded -> loaded.forEach(DownloadManager::download) } },
            additionalActions = additionalActions,
        )
    }
}

private fun mediaDownloadState(id: String): MediaDownloadState = mediaDownloadState(LibraryState.downloadStates[id])

private fun mediaDownloadState(state: DownloadState?): MediaDownloadState =
    when (state) {
        DownloadState.QUEUED,
        DownloadState.DOWNLOADING,
        -> MediaDownloadState.DOWNLOADING

        DownloadState.DOWNLOADED -> MediaDownloadState.DOWNLOADED

        else -> MediaDownloadState.NOT_DOWNLOADED
    }

@Composable
fun EpisodeContextMenu(
    episode: EpisodeItem,
    expanded: Boolean,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    onToggleSaved: (() -> Unit)? = null,
    onHideFromLibrary: (() -> Unit)? = null,
    additionalActions: List<PlayerMenuAction> = emptyList(),
) {
    MediaContextMenu(expanded = expanded, offset = offset, onDismiss = onDismiss) {
        val playback = LocalPlaybackController.current
        val song = episode.toSongItem()
        val isGuest = PlayerState.isListenTogetherGuest()
        MediaMenuModel(
            title = episode.title,
            subtitle = episode.author?.name,
            thumbnail = episode.thumbnail,
            primaryActions =
                listOf(
                    PlayerMenuAction(
                        CrystaListIcons.Filled.PlayArrow,
                        stringResource(Res.string.play),
                        enabled =
                            !isGuest && !episode.isUnavailable,
                    ) {
                        sharedMenuPlayState.cancel()
                        sharedMenuQueueAdditions.cancel()
                        PlayerState.playTrack(song)
                        onDismiss()
                    },
                    PlayerMenuAction(CrystaListIcons.AutoMirrored.Outlined.PlaylistAdd, stringResource(Res.string.add_to_playlist)) {
                        showAddToPlaylist(song)
                        onDismiss()
                    },
                    PlayerMenuAction(CrystaListIcons.Outlined.Share, stringResource(Res.string.share)) {
                        shareSong(song)
                        onDismiss()
                    },
                ),
            actionGroups =
                listOf(
                    if (isGuest) {
                        listOf(
                            PlayerMenuAction(
                                CrystaListIcons.AutoMirrored.Outlined.QueueMusic,
                                stringResource(Res.string.suggest_to_host),
                                enabled = !episode.isUnavailable,
                            ) {
                                suggestToHost(song)
                                onDismiss()
                            },
                        )
                    } else {
                        listOf(
                            PlayerMenuAction(
                                CrystaListIcons.AutoMirrored.Filled.PlaylistPlay,
                                stringResource(Res.string.play_next),
                                enabled = !episode.isUnavailable,
                            ) {
                                sharedMenuQueueAdditions.enqueue(song, listOf(song), playback) { playNext() }
                                onDismiss()
                            },
                            PlayerMenuAction(
                                CrystaListIcons.AutoMirrored.Outlined.QueueMusic,
                                stringResource(Res.string.add_to_queue),
                                enabled = !episode.isUnavailable,
                            ) {
                                sharedMenuQueueAdditions.enqueue(song, listOf(song), playback) { addToQueue() }
                                onDismiss()
                            },
                        )
                    },
                    listOfNotNull(
                        onToggleSaved?.let { action ->
                            PlayerMenuAction(
                                CrystaListIcons.Outlined.LibraryAdd,
                                if (episode.saved) {
                                    stringResource(
                                        Res.string.remove_from_saved_episodes,
                                    )
                                } else {
                                    stringResource(Res.string.save_episode)
                                },
                            ) {
                                action()
                                onDismiss()
                            }
                        },
                        speedDialAction(
                            item = episode,
                            pinLabel = stringResource(Res.string.pin_to_speed_dial),
                            unpinLabel = stringResource(Res.string.unpin_from_speed_dial),
                            onDismiss = onDismiss,
                        ),
                        onHideFromLibrary?.let { action -> hideFromLibraryAction(action, onDismiss) },
                        songDownloadAction(song, onDismiss),
                    ),
                    listOfNotNull(
                        episode.podcastId?.let { podcastId ->
                            PlayerMenuAction(CrystaListIcons.Outlined.Podcasts, stringResource(Res.string.view_podcast)) {
                                PodcastState.fetch(podcastId)
                                onDismiss()
                            }
                        },
                    ),
                    additionalActions,
                ),
        )
    }
}

@Composable
fun LibraryItemContextMenu(
    playlist: PlaylistItem?,
    expanded: Boolean,
    offset: DpOffset,
    onDismiss: () -> Unit,
    onHide: () -> Unit,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    pinnedId: String? = null,
) {
    CursorDropdownMenu(expanded, offset, onDismiss) {
        val actions =
            listOfNotNull(
                onRename?.takeIf { playlist?.isEditable == true }?.let { action ->
                    PlayerMenuAction(CrystaListIcons.Outlined.Edit, stringResource(Res.string.rename)) {
                        action()
                        onDismiss()
                    }
                },
                onDelete?.takeIf { playlist?.isEditable == true }?.let { action ->
                    PlayerMenuAction(CrystaListIcons.Outlined.Delete, stringResource(Res.string.delete_playlist)) {
                        action()
                        onDismiss()
                    }
                },
                hideFromLibraryAction(onHide, onDismiss),
            )
        Column(Modifier.width(300.dp).padding(horizontal = 8.dp)) {
            ExpressiveMenuActionGroup(actions, 0, 1)
        }
    }
}

private fun showAddToPlaylist(song: SongItem) {
    PlayerState.showAddToPlaylistSong = song
}

private fun shareSong(song: SongItem) {
    AppState.copyToClipboard(ShareUrls.song(song.id))
}

private fun suggestToHost(song: SongItem) {
    ListenTogetherState.suggestTrack(song)
}

@Composable
private fun speedDialAction(
    item: YTItem,
    pinLabel: String,
    unpinLabel: String,
    onDismiss: () -> Unit,
): PlayerMenuAction {
    val isPinned = item.id in HomeState.uiState.speedDialPinnedIds
    return PlayerMenuAction(
        if (isPinned) CrystaListIcons.Outlined.KeepOff else CrystaListIcons.Filled.PushPin,
        if (isPinned) unpinLabel else pinLabel,
    ) {
        if (isPinned) HomeState.unpinSpeedDialItem(item.id) else HomeState.pinSpeedDialItem(item)
        onDismiss()
    }
}

@Composable
private fun hideFromLibraryAction(
    action: () -> Unit,
    onDismiss: () -> Unit,
) = PlayerMenuAction(CrystaListIcons.Filled.VisibilityOff, stringResource(Res.string.hide_from_library)) {
    action()
    onDismiss()
}

@Composable
internal fun songDownloadAction(
    song: SongItem,
    onDismiss: () -> Unit,
): PlayerMenuAction {
    val state = LibraryState.downloadStates[song.id]
    return when (state) {
        DownloadState.DOWNLOADING -> {
            PlayerMenuAction(
                CrystaListIcons.Outlined.Download,
                stringResource(
                    Res.string.downloading_percent,
                    ((LibraryState.downloadProgress[song.id] ?: 0f) * 100).toInt(),
                    stringResource(Res.string.percent_symbol),
                ),
                enabled = false,
            ) {}
        }

        DownloadState.DOWNLOADED -> {
            PlayerMenuAction(
                CrystaListIcons.Outlined.OfflinePin,
                stringResource(Res.string.remove_download),
                stringResource(Res.string.delete_downloaded_audio),
            ) {
                DownloadManager.deleteDownload(song.id)
                onDismiss()
            }
        }

        else -> {
            PlayerMenuAction(
                CrystaListIcons.Outlined.Download,
                stringResource(Res.string.action_download),
                stringResource(Res.string.make_available_offline),
                enabled = !song.isUnavailable,
            ) {
                DownloadManager.download(song)
                onDismiss()
            }
        }
    }
}

private class ReservedPlaybackController(
    private val delegate: PlaybackController,
    private val expectedGeneration: Long,
) : PlaybackController by delegate {
    override fun play(
        song: SongItem,
        queue: List<SongItem>,
        songsOnly: Boolean,
    ) = delegate.playAtGeneration(expectedGeneration, song, queue, songsOnly)

    override fun playQueue(
        songs: List<SongItem>,
        startIndex: Int,
        queueTitle: String?,
    ) = delegate.playQueueAtGeneration(expectedGeneration, songs, startIndex, queueTitle)

    override fun shuffleQueue(
        songs: List<SongItem>,
        queueTitle: String?,
        songsOnly: Boolean,
    ) = delegate.shuffleQueueAtGeneration(expectedGeneration, songs, queueTitle, songsOnly)

    override fun playNext(songs: List<SongItem>) = delegate.playNextAtGeneration(expectedGeneration, songs)

    override fun addToQueue(songs: List<SongItem>) = delegate.addToQueueAtGeneration(expectedGeneration, songs)
}

private fun launchTrackedMenuPlayback(
    item: YTItem,
    existingSongs: List<SongItem>,
    playback: PlaybackController,
    pendingPlayState: PendingCollectionPlayState? = null,
    onPlaybackAction: () -> Unit = {},
    invoke: MediaMenuPlaybackActions.() -> Unit,
) {
    val expectedGeneration = playback.reserveExplicitPlaybackGeneration()

    onPlaybackAction()
    sharedMenuPlayState.cancel()
    sharedMenuQueueAdditions.cancel()
    pendingPlayState?.cancel()
    var launched: Job? = null
    val reservedPlayback = ReservedPlaybackController(playback, expectedGeneration)
    val guardedActions =
        MediaMenuPlaybackActions(
            item,
            reservedPlayback,
            withSongs = { action ->
                launched =
                    withMediaSongs(item, existingSongs) { loadedSongs ->
                        if (playback.explicitPlaybackGeneration == expectedGeneration) {
                            action(loadedSongs)
                        }
                    }
            },
        )
    guardedActions.invoke()
    sharedMenuPlayState.track(launched)
    pendingPlayState?.track(launched)
}

private fun launchTrackedMenuQueueAction(
    item: YTItem,
    existingSongs: List<SongItem>,
    playback: PlaybackController,
    invoke: MediaMenuPlaybackActions.() -> Unit,
) {
    sharedMenuQueueAdditions.enqueue(item, existingSongs, playback, invoke)
}

private fun withMediaSongs(
    item: YTItem,
    existingSongs: List<SongItem>,
    action: (List<SongItem>) -> Unit,
): Job? {
    if (existingSongs.isNotEmpty()) {
        action(existingSongs)
        return null
    }
    return AppState.scope.launch {
        try {
            loadMediaSongs(item, existingSongs).takeIf { it.isNotEmpty() }?.let(action)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppState.snackbarHostState.showSnackbar(getString(Res.string.error_unknown))
        }
    }
}

private suspend fun loadMediaSongs(
    item: YTItem,
    existingSongs: List<SongItem>,
): List<SongItem> {
    if (existingSongs.isNotEmpty()) return existingSongs
    return withContext(Dispatchers.IO) {
        if (item is PlaylistItem && item.id.startsWith("auto:")) {
            val database = GlobalContext.get().get<MusicDatabase>()
            val dao = database.dao()
            when (item.id.removePrefix("auto:")) {
                "liked" -> {
                    SongLikeState.awaitCompleteLikes().orEmpty()
                }

                "downloaded" -> {
                    LibraryState.downloadedFiles.map { it.toSongItem() }
                }

                "top100" -> {
                    dao.mostPlayedSongs(0L, limit = 100).first().map { it.localSongItem() }
                }

                "uploaded" -> {
                    GlobalYouTubeRepository.instance.getLibraryUploadSongs(limit = 500)
                }

                "cached" -> {
                    val sorting = Settings.shared.cachedCollectionSorting()
                    val customOrder = if (sorting.sort == CollectionSort.MANUAL) Settings.shared.cachedSongsOrder() else emptyList()
                    val entries = SongCacheManager.getCachedSongs()
                    val playCounts =
                        if (sorting.sort == CollectionSort.MOST_PLAYED && entries.isNotEmpty()) {
                            database.collectionPlayCounts(entries.map(CachedSongEntry::songId)).first()
                        } else {
                            emptyMap()
                        }
                    val metadata = SongCacheManager.cachedCollectionMetadata(entries)
                    entries
                        .map { entry ->
                            SongItem(
                                id = entry.songId,
                                title = entry.title,
                                artists = listOf(ArtistTiny(null, entry.artist)),
                                thumbnail = null,
                                album = null,
                                isExplicit = entry.explicitness == true,
                                explicitness = entry.explicitness,
                            )
                        }.presentCollectionWithPlayCounts(
                            sort = sorting.sort,
                            direction = sorting.direction,
                            customOrder = customOrder,
                            metadata = metadata,
                            playCounts = playCounts,
                        )
                }

                else -> {
                    emptyList()
                }
            }
        } else if (item is PlaylistItem) {
            val database = GlobalContext.get().get<MusicDatabase>()
            database.refreshHistoryPlaylist(item.id)
            val dao = database.dao()
            val storedPlaylist = dao.findPlaylistEntity(item.id)
            if (item.isLocal || storedPlaylist?.isLocal == true || (storedPlaylist != null && storedPlaylist.browseId == null)) {
                storedPlaylist
                    ?.let { playlist ->
                        dao.playlistSongs(playlist.id).first().map { entry ->
                            entry.song.localSongItem().copy(playlistSetVideoId = entry.map.setVideoId)
                        }
                    }.orEmpty()
            } else {
                BlockedContent.filterSongs(
                    GlobalContext
                        .get()
                        .get<MusicDatabase>()
                        .loadAndCachePlaylist(GlobalYouTubeRepository.instance, item.id)
                        .playlistSongs(),
                )
            }
        } else {
            loadMediaItemSongs(item)
        }
    }
}

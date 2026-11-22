@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.crystalist.android.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crystalist.android.LocalMiniPlayerBottomInset
import com.crystalist.android.playback.LocalPlayerConnection
import com.crystalist.android.ui.components.handleItemClick
import com.crystalist.android.viewmodels.HomeSectionViewModel
import com.crystalist.android.viewmodels.HomeViewModel
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.error
import com.crystalist.shared.generated.resources.play
import com.crystalist.shared.generated.resources.shuffle
import com.crystalist.shared.model.BrowsePageData
import com.crystalist.shared.model.HomeSection
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.YTItem
import com.crystalist.shared.model.isAvailableOffline
import com.crystalist.shared.model.isVideoShelf
import com.crystalist.shared.settings.BlockedContent
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.screens.BrowseContent
import com.crystalist.shared.ui.screens.HomeShelfBuilder
import com.crystalist.shared.ui.screens.homeContentText
import com.crystalist.shared.ui.screens.pageKey
import com.crystalist.shared.ui.screens.toHomeSection
import com.crystalist.shared.ui.tv.tvFocusHighlight
import com.crystalist.shared.youtube.playback.PlaybackNetworkStateStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeSectionScreen(
    sectionKey: String,
    title: String,
    section: HomeSection? = null,
    onBackClick: () -> Unit = {},
    onNavigateToAlbum: (String) -> Unit = {},
    onNavigateToArtist: (String) -> Unit = {},
    onNavigateToPlaylist: (String, String?) -> Unit = { _, _ -> },
    onNavigateToPodcast: (String) -> Unit = {},
    onNavigateToBrowse: (String, String?, String) -> Unit = { _, _, _ -> },
    homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = checkNotNull(LocalActivity.current as? ComponentActivity)),
) {
    val homeState by homeViewModel.shared.uiState.collectAsState()
    val text = homeContentText()
    val width = LocalConfiguration.current.screenWidthDp.dp
    val initialSection =
        section ?: HomeShelfBuilder
            .build(homeState, text, width, includeHidden = true)
            .firstOrNull { it.pageKey == sectionKey }
            ?.toHomeSection()
    if (initialSection == null) {
        BrowseContent(
            page = BrowsePageData(title = title),
            sectionPage = true,
            isLoading = homeState.isLoading || homeState.isRestoringCache,
            isLoadingMore = false,
            onBackClick = onBackClick,
            onItemClick = { _, _ -> },
            onItemLongClick = null,
            onLoadMore = {},
            error = stringResource(Res.string.error).takeIf { homeState.error != null },
            onRetry = { homeViewModel.shared.refresh {} },
            contentPadding = PaddingValues(bottom = LocalMiniPlayerBottomInset.current),
        )
        return
    }
    val pageViewModel = viewModel(key = sectionKey) { HomeSectionViewModel(initialSection) }
    val model = pageViewModel.shared
    val blockedVersion by BlockedContent.version.collectAsState()
    val page = model.page
    val visiblePage = remember(page, blockedVersion) { page.copy(sections = BlockedContent.filterSections(page.sections)) }
    val playerConnection = LocalPlayerConnection.current
    val currentSong = playerConnection?.currentSong?.collectAsState() ?: remember { mutableStateOf(null) }
    val isPlaying = playerConnection?.isPlaying?.collectAsState() ?: remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var selectedMenuItem by remember { mutableStateOf<YTItem?>(null) }
    val database: MusicDatabase = koinInject()
    val network by PlaybackNetworkStateStore.state.collectAsState()
    val cachedIds by remember(database) {
        database
            .dao()
            .cachedAudioIds()
            .map { it.toSet() }
            .distinctUntilChanged()
    }.collectAsState(initial = emptySet())
    val isItemEnabled: (YTItem) -> Boolean = { !it.isUnavailable && (network.connected || it.isAvailableOffline(cachedIds)) }
    val songs =
        visiblePage.sections
            .flatMap { it.items }
            .filterIsInstance<SongItem>()
            .filter(isItemEnabled)
    val showSongActions = songs.isNotEmpty() && visiblePage.sections.all { shelf -> shelf.items.all { it is SongItem } }
    val loadError = stringResource(Res.string.error)

    BrowseContent(
        page = visiblePage,
        title = title,
        sectionPage = true,
        isLoading = model.isLoading,
        isLoadingMore = model.isLoadingMore,
        hasMore = model.hasMore,
        onBackClick = onBackClick,
        onItemClick = { item, queue ->
            handleItemClick(
                item,
                onNavigateToAlbum,
                onNavigateToArtist,
                onNavigateToPlaylist,
                onNavigateToPodcast,
                playerConnection,
                scope,
                onNavigateToBrowse,
                queue.filter(isItemEnabled),
                startVideo = visiblePage.sections.any { item in it.items && it.items.isVideoShelf() },
            )
        },
        onItemLongClick = { selectedMenuItem = it },
        onLoadMore = model::loadMore,
        error = loadError.takeIf { model.loadFailed },
        onRetry = model::load,
        loadMoreError = loadError.takeIf { model.loadMoreFailed },
        contentPadding = PaddingValues(bottom = LocalMiniPlayerBottomInset.current),
        currentSongId = remember(currentSong) { derivedStateOf { currentSong.value?.id } },
        isPlaying = isPlaying,
        onArtistClick = onNavigateToArtist,
        onAlbumClick = onNavigateToAlbum,
        onAlbumPlay = { playerConnection?.playAlbum(it.id, it.title) },
        isItemEnabled = isItemEnabled,
        headerContent =
            if (showSongActions) {
                {
                    HomeSectionActions(
                        onPlayAll = { playerConnection?.playQueue(songs, queueTitle = title, startVideo = false) },
                        onShuffle = { playerConnection?.shuffleQueue(songs, queueTitle = title) },
                    )
                }
            } else {
                null
            },
    )
    HomeItemMenu(
        selectedItem = selectedMenuItem,
        onDismiss = { selectedMenuItem = null },
        onNavigateToAlbum = onNavigateToAlbum,
        onNavigateToArtist = onNavigateToArtist,
        onNavigateToPodcast = onNavigateToPodcast,
        onSongMetadataUpdated = { song ->
            homeViewModel.shared.updateSongMetadata(song)
            pageViewModel.updateSongMetadata(song)
        },
    )
}

@Composable
internal fun HomeSectionActions(
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier.weight(1f).tvFocusHighlight(),
                shapes = ButtonDefaults.shapes(),
            ) {
                Icon(CrystaListIcons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.play))
            }
            FilledTonalButton(
                onClick = onShuffle,
                modifier = Modifier.weight(1f).tvFocusHighlight(),
                shapes = ButtonDefaults.shapes(),
            ) {
                Icon(CrystaListIcons.Rounded.Shuffle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.shuffle))
            }
        }

        HorizontalDivider(Modifier.padding(top = 18.dp))
    }
}

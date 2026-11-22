package com.crystalist.desktop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.state.content.ArtistState
import com.crystalist.desktop.utils.saveArtistPhoto
import com.crystalist.shared.model.ArtistItem
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.ui.navigation.LocalNavigator
import com.crystalist.shared.ui.screens.AboutArtistContent
import com.crystalist.shared.ui.screens.ArtistPhotoActions
import com.crystalist.shared.viewmodels.AboutArtistViewModel
import com.crystalist.shared.viewmodels.artistCatalogTitles
import kotlinx.coroutines.launch

@Composable
fun AboutArtistScreenHost(
    artistId: String,
    artistName: String,
    initialInfo: ArtistItem? = null,
) {
    val navigator = LocalNavigator.current
    val scope = rememberCoroutineScope()
    val viewModel = remember(artistId, artistName) { AboutArtistViewModel(artistId, artistName, scope) }
    val uiState by viewModel.uiState.collectAsState()
    val catalogTitles = remember(ArtistState.artistData) { artistCatalogTitles(ArtistState.artistData) }
    LaunchedEffect(viewModel, catalogTitles, ArtistState.isArtistLoading, Settings.shared.lastResolvedContentLanguage) {
        if (!ArtistState.isArtistLoading) viewModel.load(catalogTitles)
    }
    val photoActions =
        remember(artistName) {
            ArtistPhotoActions(
                share = { AppState.shareLink(it, artistName) },
                save = { saveArtistPhoto(it, artistName) },
                showMessage = { message -> AppState.scope.launch { AppState.snackbarHostState.showSnackbar(message) } },
            )
        }
    AboutArtistContent(
        artistName = artistName,
        info = ArtistState.artistData["header"]?.firstOrNull() as? ArtistItem ?: initialInfo,
        uiState = uiState,
        photoActions = photoActions,
        onBackClick = { navigator.goBack() },
    )
}

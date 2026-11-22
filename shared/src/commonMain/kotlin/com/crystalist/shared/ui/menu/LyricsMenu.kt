@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.crystalist.shared.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.crystalist.shared.api.lyrics.LyricsUtils
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.ai_translation_for_track
import com.crystalist.shared.generated.resources.cancel
import com.crystalist.shared.generated.resources.copied
import com.crystalist.shared.generated.resources.copy
import com.crystalist.shared.generated.resources.edit
import com.crystalist.shared.generated.resources.edit_lyrics
import com.crystalist.shared.generated.resources.lyrics_offset
import com.crystalist.shared.generated.resources.lyrics_romanization_show_original
import com.crystalist.shared.generated.resources.lyrics_romanize_title
import com.crystalist.shared.generated.resources.lyrics_show_translations
import com.crystalist.shared.generated.resources.no_provider_results_found
import com.crystalist.shared.generated.resources.offset_ms
import com.crystalist.shared.generated.resources.refetch
import com.crystalist.shared.generated.resources.save
import com.crystalist.shared.generated.resources.sources
import com.crystalist.shared.model.LyricsProvider
import com.crystalist.shared.model.LyricsProviderResult
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.previewAspectRatio
import com.crystalist.shared.model.previewThumbnailUrl
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.settings.displayName
import com.crystalist.shared.ui.components.ExpressiveSwitch
import com.crystalist.shared.ui.components.Material3MenuGroup
import com.crystalist.shared.ui.components.Material3MenuItemData
import com.crystalist.shared.ui.components.ModalBottomSheet
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.components.PlayerMenuPrimaryActions
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.components.lyrics.LyricsEditorScreen
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.theme.ComponentShapes
import com.crystalist.shared.ui.utils.setText
import com.crystalist.shared.ui.utils.verticalScrollWithDrag
import com.crystalist.shared.viewmodels.LyricsUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsMenu(
    song: SongItem,
    lyricsUiState: LyricsUiState,
    onDismiss: () -> Unit,
    onRefetch: (customTitle: String?, customArtist: String?) -> Unit,
    onLoadProviders: (customTitle: String?, customArtist: String?, provider: LyricsProvider?) -> Unit,
    onSelectProvider: (LyricsProviderResult) -> Unit,
    onShowLyricsOffset: () -> Unit,
    onLyricsChanged: () -> Unit,
    onManualAiTranslationChange: ((Boolean) -> Unit)? = null,
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    var editorSong by remember { mutableStateOf(song.id to song.title) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showProviderDialog by remember(song.id) { mutableStateOf(false) }
    var showSearchDialog by remember(song.id) { mutableStateOf(false) }
    var showRefreshMenu by remember(song.id) { mutableStateOf(false) }
    var providerDialogFromRefetch by remember(song.id) { mutableStateOf(false) }

    val queryTitle = lyricsUiState.queryTitle ?: song.title
    val queryArtist = lyricsUiState.queryArtist ?: song.artists.joinToString { it.name }
    var editText by remember(lyricsUiState.rawLyrics) { mutableStateOf(TextFieldValue(lyricsUiState.rawLyrics.orEmpty())) }
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedMessage = stringResource(Res.string.copied)
    val romanizeAsMain = Settings.shared.isLyricsRomanizationActive && Settings.shared.lyricsRomanizeAsMain
    val toggleChecked =
        if (romanizeAsMain) {
            Settings.shared.lyricsShowOriginalLyrics
        } else {
            Settings.shared.lyricsRomanizationEnabled
        }
    var manualAiTranslationChecked by remember(lyricsUiState.manualAiTranslationEnabled) {
        mutableStateOf(lyricsUiState.manualAiTranslationEnabled)
    }

    PopupVisibility(visible = showEditDialog) {
        LyricsEditorScreen(
            songId = editorSong.first,
            songTitle = editorSong.second,
            rawLyrics = editText.text,
            onChanged = onLyricsChanged,
            onDismiss = {
                showEditDialog = false
                onDismiss()
            },
        )
    }

    PopupVisibility(visible = showSearchDialog) {
        LyricsSearchDialog(
            initialTitle = queryTitle,
            initialArtist = queryArtist,
            onDismiss = { showSearchDialog = false },
            onSearch = { title, artist ->
                onLoadProviders(title, artist, null)
                showSearchDialog = false
                providerDialogFromRefetch = false
                showProviderDialog = true
            },
        )
    }

    PopupVisibility(visible = showRefreshMenu) {
        LyricsRefreshDialog(
            onDismiss = { showRefreshMenu = false },
            onAutomatic = {
                onRefetch(queryTitle, queryArtist)
                onDismiss()
            },
            onProvider = { provider ->
                onLoadProviders(queryTitle, queryArtist, provider)
                providerDialogFromRefetch = true
                showProviderDialog = true
            },
        )
    }

    PopupVisibility(visible = showProviderDialog) {
        LyricsProviderDialog(
            search = lyricsUiState.providerSearch,
            noResultsText = stringResource(Res.string.no_provider_results_found),
            onDismiss = {
                showProviderDialog = false
                if (providerDialogFromRefetch) {
                    providerDialogFromRefetch = false
                    showRefreshMenu = true
                }
            },
            onSelect = { result ->
                onSelectProvider(result)
                providerDialogFromRefetch = false
                showProviderDialog = false
                onDismiss()
            },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ComponentShapes.BottomSheetShape,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScrollWithDrag(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ExpressiveMediaMenuHeader(
                title = song.title,
                subtitle = song.artists.joinToString { it.displayName },
                thumbnail = song.previewThumbnailUrl,
                thumbnailAspectRatio = song.previewAspectRatio,
            )

            PlayerMenuPrimaryActions(
                actions =
                    listOf(
                        PlayerMenuAction(
                            icon = CrystaListIcons.Outlined.Edit,
                            title = stringResource(Res.string.edit),
                            onClick = {
                                editText = TextFieldValue(LyricsUtils.lyricsForEditing(lyricsUiState.rawLyrics, lyricsUiState.lyrics))
                                editorSong = song.id to song.title
                                showEditDialog = true
                            },
                        ),
                        PlayerMenuAction(
                            icon = CrystaListIcons.Outlined.Cached,
                            title = stringResource(Res.string.refetch),
                            onClick = { showRefreshMenu = true },
                        ),
                        PlayerMenuAction(
                            icon = CrystaListIcons.Outlined.Search,
                            title = stringResource(Res.string.sources),
                            onClick = {
                                showSearchDialog = true
                            },
                        ),
                    ),
            )

            Material3MenuGroup(
                items =
                    listOfNotNull(
                        if (Settings.shared.lyricsAiTranslationEnabled) {
                            Material3MenuItemData(
                                title = { Text(stringResource(Res.string.ai_translation_for_track)) },
                                icon = {
                                    Icon(
                                        imageVector = CrystaListIcons.Outlined.Translate,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    manualAiTranslationChecked = !manualAiTranslationChecked
                                    onManualAiTranslationChange?.invoke(manualAiTranslationChecked)
                                },
                                trailingContent = {
                                    ExpressiveSwitch(
                                        checked = manualAiTranslationChecked,
                                        onCheckedChange = {
                                            manualAiTranslationChecked = it
                                            onManualAiTranslationChange?.invoke(it)
                                        },
                                    )
                                },
                            )
                        } else {
                            null
                        },
                        if (lyricsUiState.lyrics.any {
                                !it.translatedText.isNullOrBlank() || !it.backgroundTranslatedText.isNullOrBlank()
                            }
                        ) {
                            Material3MenuItemData(
                                title = { Text(stringResource(Res.string.lyrics_show_translations)) },
                                icon = {
                                    Icon(
                                        imageVector = CrystaListIcons.Outlined.Translate,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    Settings.shared.lyricsShowTranslations = !Settings.shared.lyricsShowTranslations
                                },
                                trailingContent = {
                                    ExpressiveSwitch(
                                        checked = Settings.shared.lyricsShowTranslations,
                                        onCheckedChange = { Settings.shared.lyricsShowTranslations = it },
                                    )
                                },
                            )
                        } else {
                            null
                        },
                        Material3MenuItemData(
                            title = {
                                Text(
                                    stringResource(
                                        if (romanizeAsMain) {
                                            Res.string.lyrics_romanization_show_original
                                        } else {
                                            Res.string.lyrics_romanize_title
                                        },
                                    ),
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = CrystaListIcons.Outlined.Translate,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                if (romanizeAsMain) {
                                    Settings.shared.lyricsShowOriginalLyrics = !toggleChecked
                                } else {
                                    Settings.shared.lyricsRomanizationEnabled = !toggleChecked
                                }
                            },
                            trailingContent = {
                                ExpressiveSwitch(
                                    checked = toggleChecked,
                                    onCheckedChange = {
                                        if (romanizeAsMain) {
                                            Settings.shared.lyricsShowOriginalLyrics = it
                                        } else {
                                            Settings.shared.lyricsRomanizationEnabled = it
                                        }
                                    },
                                )
                            },
                        ),
                        if (lyricsUiState.lyrics.any { it.time > 0L }) {
                            Material3MenuItemData(
                                title = { Text(stringResource(Res.string.lyrics_offset)) },
                                description = {
                                    val sign = if (lyricsUiState.timingOffsetMs > 0) "+" else ""
                                    Text(sign + stringResource(Res.string.offset_ms, lyricsUiState.timingOffsetMs))
                                },
                                icon = {
                                    Icon(
                                        imageVector = CrystaListIcons.Outlined.FastForward,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    onDismiss()
                                    onShowLyricsOffset()
                                },
                            )
                        } else {
                            null
                        },
                        Material3MenuItemData(
                            title = { Text(stringResource(Res.string.copy)) },
                            description = {
                                Text(
                                    lyricsUiState.lyrics
                                        .firstOrNull()
                                        ?.text
                                        .orEmpty(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = CrystaListIcons.Outlined.ContentCopy,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                val textToCopy =
                                    LyricsUtils.toPlainText(lyricsUiState.lyrics)
                                coroutineScope.launch {
                                    clipboard.setText(textToCopy)
                                    snackbarHostState.showSnackbar(copiedMessage)
                                }
                            },
                        ),
                    ),
            )

            SnackbarHost(snackbarHostState)
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.crystalist.android.ui.menu

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.crystalist.android.ui.component.AdaptiveModalBottomSheet
import com.crystalist.android.ui.component.Material3MenuGroup
import com.crystalist.android.ui.component.Material3MenuItemData
import com.crystalist.shared.api.lyrics.LyricsUtils
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.ai_translation_for_track
import com.crystalist.shared.generated.resources.ai_translation_new_desc
import com.crystalist.shared.generated.resources.ai_translation_new_title
import com.crystalist.shared.generated.resources.cancel
import com.crystalist.shared.generated.resources.copied
import com.crystalist.shared.generated.resources.copy
import com.crystalist.shared.generated.resources.edit
import com.crystalist.shared.generated.resources.edit_lyrics
import com.crystalist.shared.generated.resources.lyrics_offset
import com.crystalist.shared.generated.resources.lyrics_romanization_show_original
import com.crystalist.shared.generated.resources.lyrics_romanize_title
import com.crystalist.shared.generated.resources.lyrics_show_translations
import com.crystalist.shared.generated.resources.no_lyrics_provider_results
import com.crystalist.shared.generated.resources.refetch
import com.crystalist.shared.generated.resources.save
import com.crystalist.shared.generated.resources.sources
import com.crystalist.shared.model.LyricsEntry
import com.crystalist.shared.model.LyricsProvider
import com.crystalist.shared.model.LyricsProviderResult
import com.crystalist.shared.repository.LyricsProviderSearch
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.ExpressiveSwitch
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.components.PlayerMenuPrimaryActions
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.components.lyrics.LyricsEditorScreen
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.menu.ExpressiveMediaMenuHeader
import com.crystalist.shared.ui.menu.LyricsProviderDialog
import com.crystalist.shared.ui.menu.LyricsRefreshDialog
import com.crystalist.shared.ui.menu.LyricsSearchDialog
import com.crystalist.shared.ui.theme.ComponentShapes
import com.crystalist.shared.ui.utils.setText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsMenu(
    songId: String,
    lyrics: List<LyricsEntry>,
    rawLyrics: String? = null,
    songTitle: String,
    artistName: String,
    searchTitle: String = songTitle,
    searchArtist: String = artistName,
    artworkUrl: String? = null,
    lyricsOffsetMs: Int = 0,
    manualAiTranslationEnabled: Boolean = false,
    providerSearch: LyricsProviderSearch? = null,
    onDismiss: () -> Unit,
    onRefetchLyrics: ((title: String?, artist: String?) -> Unit)? = null,
    onLoadProviderResults: ((title: String?, artist: String?, provider: LyricsProvider?) -> Unit)? = null,
    onSelectProviderResult: ((LyricsProviderResult) -> Unit)? = null,
    onShowLyricsOffset: (() -> Unit)? = null,
    onManualAiTranslationChange: (Boolean) -> Unit = {},
    onLyricsChanged: (() -> Unit)? = null,
    onNewAiTranslation: () -> Unit = {},
) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(Res.string.copied)
    var showSearchDialog by rememberSaveable(songId) { mutableStateOf(false) }
    var editorSong by remember { mutableStateOf(songId to songTitle) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var editText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = rawLyrics.orEmpty())) }
    var showProviderDialog by rememberSaveable(songId) { mutableStateOf(false) }
    var showRefreshMenu by remember(songId) { mutableStateOf(false) }
    var providerDialogFromRefetch by rememberSaveable(songId) { mutableStateOf(false) }

    PopupVisibility(visible = showSearchDialog) {
        LyricsSearchDialog(
            initialTitle = searchTitle,
            initialArtist = searchArtist,
            onDismiss = { showSearchDialog = false },
            onSearch = { title, artist ->
                onLoadProviderResults?.invoke(title, artist, null)
                showSearchDialog = false
                providerDialogFromRefetch = false
                showProviderDialog = true
            },
        )
    }

    val romanizeAsMain = Settings.shared.isLyricsRomanizationActive && Settings.shared.lyricsRomanizeAsMain
    val toggleChecked =
        if (romanizeAsMain) {
            Settings.shared.lyricsShowOriginalLyrics
        } else {
            Settings.shared.lyricsRomanizationEnabled
        }
    var manualAiTranslationChecked by remember(manualAiTranslationEnabled) { mutableStateOf(manualAiTranslationEnabled) }

    PopupVisibility(visible = showEditDialog) {
        LyricsEditorScreen(
            songId = editorSong.first,
            songTitle = editorSong.second,
            rawLyrics = editText.text,
            onChanged = { onLyricsChanged?.invoke() },
            onDismiss = {
                showEditDialog = false
                onDismiss()
            },
        )
    }

    PopupVisibility(visible = showRefreshMenu) {
        LyricsRefreshDialog(
            onDismiss = { showRefreshMenu = false },
            onAutomatic = {
                onRefetchLyrics?.invoke(searchTitle, searchArtist)
                onDismiss()
            },
            onProvider = { provider ->
                onLoadProviderResults?.invoke(searchTitle, searchArtist, provider)
                providerDialogFromRefetch = true
                showProviderDialog = true
            },
        )
    }

    PopupVisibility(visible = showProviderDialog) {
        LyricsProviderDialog(
            search = providerSearch,
            noResultsText = stringResource(Res.string.no_lyrics_provider_results),
            onDismiss = {
                showProviderDialog = false
                if (providerDialogFromRefetch) {
                    providerDialogFromRefetch = false
                    showRefreshMenu = true
                }
            },
            onSelect = { result ->
                onSelectProviderResult?.invoke(result)
                providerDialogFromRefetch = false
                showProviderDialog = false
                onDismiss()
            },
        )
    }

    val menuCardColors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )

    AdaptiveModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ComponentShapes.BottomSheetShape,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding =
                PaddingValues(
                    bottom = 8.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                ),
        ) {
            item {
                ExpressiveMediaMenuHeader(
                    title = songTitle,
                    subtitle = artistName,
                    thumbnail = artworkUrl,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            item {
                PlayerMenuPrimaryActions(
                    actions =
                        listOfNotNull(
                            onLyricsChanged?.let {
                                PlayerMenuAction(
                                    icon = CrystaListIcons.Outlined.Edit,
                                    title = stringResource(Res.string.edit),
                                    onClick = {
                                        editText = TextFieldValue(LyricsUtils.lyricsForEditing(rawLyrics, lyrics))
                                        editorSong = songId to songTitle
                                        showEditDialog = true
                                    },
                                )
                            },
                            onRefetchLyrics?.let { refetch ->
                                PlayerMenuAction(
                                    icon = CrystaListIcons.Outlined.Cached,
                                    title = stringResource(Res.string.refetch),
                                    onClick = {
                                        if (onLoadProviderResults != null) {
                                            showRefreshMenu = true
                                        } else {
                                            refetch(searchTitle, searchArtist)
                                            onDismiss()
                                        }
                                    },
                                )
                            },
                            onLoadProviderResults?.let {
                                PlayerMenuAction(
                                    icon = CrystaListIcons.Outlined.Search,
                                    title = stringResource(Res.string.sources),
                                    onClick = {
                                        showSearchDialog = true
                                    },
                                )
                            },
                            PlayerMenuAction(
                                icon = CrystaListIcons.Outlined.ContentCopy,
                                title = stringResource(Res.string.copy),
                                onClick = {
                                    scope.launch {
                                        clipboard.setText(LyricsUtils.toPlainText(lyrics))
                                        Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                                    }
                                },
                            ),
                        ),
                )
            }

            item {
                Material3MenuGroup(
                    items =
                        listOfNotNull(
                            Material3MenuItemData(
                                title = { Text(stringResource(Res.string.ai_translation_for_track)) },
                                cardColors = menuCardColors,
                                icon = {
                                    Icon(
                                        imageVector = CrystaListIcons.Outlined.Translate,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    if (Settings.shared.lyricsAiTranslationEnabled) {
                                        manualAiTranslationChecked = !manualAiTranslationChecked
                                        onManualAiTranslationChange(manualAiTranslationChecked)
                                    }
                                },
                                trailingContent = {
                                    ExpressiveSwitch(
                                        checked = manualAiTranslationChecked,
                                        enabled = Settings.shared.lyricsAiTranslationEnabled,
                                        onCheckedChange = {
                                            manualAiTranslationChecked = it
                                            onManualAiTranslationChange(it)
                                        },
                                    )
                                },
                            ),
                            if (manualAiTranslationChecked) {
                                Material3MenuItemData(
                                    title = { Text(stringResource(Res.string.ai_translation_new_title)) },
                                    description = { Text(stringResource(Res.string.ai_translation_new_desc)) },
                                    cardColors = menuCardColors,
                                    icon = {
                                        Icon(
                                            imageVector = CrystaListIcons.Outlined.Cached,
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        onNewAiTranslation()
                                    },
                                )
                            } else {
                                null
                            },
                            if (lyrics.any { !it.translatedText.isNullOrBlank() || !it.backgroundTranslatedText.isNullOrBlank() }) {
                                Material3MenuItemData(
                                    title = { Text(stringResource(Res.string.lyrics_show_translations)) },
                                    cardColors = menuCardColors,
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
                                cardColors = menuCardColors,
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
                            onShowLyricsOffset?.takeIf { lyrics.any { it.time > 0L } }?.let { showOffset ->
                                Material3MenuItemData(
                                    title = { Text(stringResource(Res.string.lyrics_offset)) },
                                    description = { Text("${lyricsOffsetMs}ms") },
                                    cardColors = menuCardColors,
                                    icon = {
                                        Icon(
                                            imageVector = CrystaListIcons.Outlined.FastForward,
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        onDismiss()
                                        showOffset()
                                    },
                                )
                            },
                        ),
                )
            }
        }
    }
}

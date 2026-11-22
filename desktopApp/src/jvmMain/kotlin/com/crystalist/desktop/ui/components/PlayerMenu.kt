/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.crystalist.desktop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.state.content.ArtistState
import com.crystalist.desktop.state.content.PlaylistAlbumState
import com.crystalist.desktop.state.playback.ListenTogetherState
import com.crystalist.desktop.state.playback.LyricsState
import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.shared.api.lyrics.LyricsUtils
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.add_romanization_below_lyrics
import com.crystalist.shared.generated.resources.ai_translation
import com.crystalist.shared.generated.resources.automatic_mode
import com.crystalist.shared.generated.resources.cancel
import com.crystalist.shared.generated.resources.copy
import com.crystalist.shared.generated.resources.details
import com.crystalist.shared.generated.resources.display_gaps_between_sections
import com.crystalist.shared.generated.resources.edit
import com.crystalist.shared.generated.resources.edit_lyrics
import com.crystalist.shared.generated.resources.edit_song_description
import com.crystalist.shared.generated.resources.equalizer
import com.crystalist.shared.generated.resources.exit_fullscreen
import com.crystalist.shared.generated.resources.follow_agent_positioning_hints
import com.crystalist.shared.generated.resources.fullscreen
import com.crystalist.shared.generated.resources.hide_lyrics
import com.crystalist.shared.generated.resources.hide_queue
import com.crystalist.shared.generated.resources.lyrics_offset
import com.crystalist.shared.generated.resources.lyrics_respect_agent_positioning
import com.crystalist.shared.generated.resources.lyrics_romanization_show_original
import com.crystalist.shared.generated.resources.lyrics_romanization_show_original_description
import com.crystalist.shared.generated.resources.lyrics_show_translations
import com.crystalist.shared.generated.resources.manual_translate_this_track
import com.crystalist.shared.generated.resources.no_provider_results_found
import com.crystalist.shared.generated.resources.no_track_playing
import com.crystalist.shared.generated.resources.playback_speed
import com.crystalist.shared.generated.resources.refetch
import com.crystalist.shared.generated.resources.romanize_track
import com.crystalist.shared.generated.resources.save
import com.crystalist.shared.generated.resources.search
import com.crystalist.shared.generated.resources.select_to_cancel
import com.crystalist.shared.generated.resources.show_interval_indicator
import com.crystalist.shared.generated.resources.show_lyrics
import com.crystalist.shared.generated.resources.show_queue
import com.crystalist.shared.generated.resources.sleep_label
import com.crystalist.shared.generated.resources.sleep_timer
import com.crystalist.shared.generated.resources.stop_music_after_set_time
import com.crystalist.shared.generated.resources.stopping_after_current_song
import com.crystalist.shared.generated.resources.tempo_and_pitch
import com.crystalist.shared.generated.resources.view_song_info
import com.crystalist.shared.listentogether.ListenTogetherRoomRole
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.settings.displayName
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.ExpressiveSwitch
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.components.TempoPitchControls
import com.crystalist.shared.ui.components.lyrics.LyricsEditorScreen
import com.crystalist.shared.ui.components.playbackMarquee
import com.crystalist.shared.ui.equalizer.EqualizerDialog
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.menu.CursorDropdownMenu
import com.crystalist.shared.ui.menu.LyricsProviderDialog
import com.crystalist.shared.ui.menu.LyricsRefreshDialog
import com.crystalist.shared.ui.menu.LyricsSearchDialog
import com.crystalist.shared.ui.menu.MediaContextMenu
import com.crystalist.shared.ui.menu.albumNavigationMenuAction
import com.crystalist.shared.ui.menu.artistLinksText
import com.crystalist.shared.ui.menu.artistNavigationMenuAction
import com.crystalist.shared.ui.menu.blockArtistMenuAction
import com.crystalist.shared.ui.menu.dislikeMenuAction
import com.crystalist.shared.ui.menu.equalizerMenuAction
import com.crystalist.shared.ui.menu.rememberDeleteUploadAction
import com.crystalist.shared.ui.menu.songPrimaryActions
import com.crystalist.shared.ui.menu.suggestToHostMenuAction
import com.crystalist.shared.ui.menu.tempoPitchMenuAction
import com.crystalist.shared.ui.playback.LocalPlaybackController
import com.crystalist.shared.utils.ShareUrls
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext

@Composable
fun LyricsActionsMenuCard(
    expanded: Boolean,
    colorScheme: ColorScheme,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onShowLyricsOffset: () -> Unit = { LyricsState.showLyricsOffsetEditor = true },
) {
    var respectAgentPositioning by remember { mutableStateOf(Settings.shared.respectAgentPositioning) }
    var showIntervalIndicator by remember { mutableStateOf(true) }
    var aiTranslationChecked by remember(LyricsState.manualAiTranslationEnabled) { mutableStateOf(LyricsState.manualAiTranslationEnabled) }
    var showTranslationsChecked by remember { mutableStateOf(Settings.shared.lyricsShowTranslations) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf("") }
    var editingTrack by remember { mutableStateOf<SongItem?>(null) }
    PopupVisibility(visible = showEditDialog && editingTrack != null) {
        editingTrack?.let { track ->
            LyricsEditorScreen(
                songId = track.id,
                songTitle = track.title,
                rawLyrics = editText,
                onChanged = { LyricsState.refreshEditedLyrics(track) },
                onDismiss = {
                    showEditDialog = false
                    onDismiss()
                },
            )
        }
    }

    val lyricsTrack = PlayerState.currentTrack
    var showSearchDialog by remember(lyricsTrack?.id) { mutableStateOf(false) }
    var showProviderDialog by remember(lyricsTrack?.id) { mutableStateOf(false) }
    var showRefreshMenu by remember(lyricsTrack?.id) { mutableStateOf(false) }
    var providerDialogFromRefetch by remember(lyricsTrack?.id) { mutableStateOf(false) }
    PopupVisibility(visible = showSearchDialog) {
        val currentTrack = PlayerState.currentTrack
        LyricsSearchDialog(
            initialTitle = LyricsState.queryTitle ?: currentTrack?.title.orEmpty(),
            initialArtist =
                LyricsState.queryArtist
                    ?: currentTrack
                        ?.artists
                        ?.joinToString { it.name }
                        .orEmpty(),
            onDismiss = { showSearchDialog = false },
            onSearch = { title, artist ->
                currentTrack?.let { LyricsState.fetchProviderResults(it, title, artist) }
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
                lyricsTrack?.let { track ->
                    LyricsState.fetchLyrics(
                        track,
                        skipCache = true,
                        customTitle = LyricsState.queryTitle,
                        customArtist = LyricsState.queryArtist,
                    )
                }
                onDismiss()
            },
            onProvider = { provider ->
                lyricsTrack?.let { track ->
                    LyricsState.fetchProviderResults(
                        track,
                        customTitle = LyricsState.queryTitle,
                        customArtist = LyricsState.queryArtist,
                        selectedProvider = provider,
                    )
                    providerDialogFromRefetch = true
                    showProviderDialog = true
                }
            },
        )
    }

    PopupVisibility(visible = showProviderDialog) {
        LyricsProviderDialog(
            search = LyricsState.providerSearch,
            noResultsText = stringResource(Res.string.no_provider_results_found),
            onDismiss = {
                showProviderDialog = false
                if (providerDialogFromRefetch) {
                    providerDialogFromRefetch = false
                    showRefreshMenu = true
                }
            },
            onSelect = { result ->
                lyricsTrack?.let { LyricsState.selectProviderResult(it, result) }
                providerDialogFromRefetch = false
                showProviderDialog = false
                onDismiss()
            },
        )
    }

    CursorDropdownMenu(
        expanded = expanded,
        offset = DpOffset.Zero,
        onDismissRequest = onDismiss,
        modifier = modifier.width(320.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LyricsActionChip(
                    icon = CrystaListIcons.Outlined.Edit,
                    label = stringResource(Res.string.edit),
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f),
                ) {
                    editText = LyricsUtils.lyricsForEditing(LyricsState.currentLyrics, LyricsState.currentLyricsEntries)
                    editingTrack = PlayerState.currentTrack
                    showEditDialog = true
                }
                LyricsActionChip(
                    icon = CrystaListIcons.Outlined.Cached,
                    label = stringResource(Res.string.refetch),
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f),
                ) {
                    showRefreshMenu = true
                }
                LyricsActionChip(
                    icon = CrystaListIcons.Outlined.Search,
                    label = stringResource(Res.string.search),
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f),
                ) {
                    showSearchDialog = true
                }
                LyricsActionChip(
                    icon = CrystaListIcons.Outlined.ContentCopy,
                    label = stringResource(Res.string.copy),
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f),
                ) {
                    val text = LyricsUtils.toPlainText(LyricsState.currentLyricsEntries)
                    if (text.isNotBlank()) {
                        AppState.copyToClipboard(text)
                    }
                    onDismiss()
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = colorScheme.onSurface.copy(alpha = 0.12f),
            )

            if (LyricsState.currentLyricsEntries.any { it.time > 0L }) {
                LyricsActionRow(
                    icon = CrystaListIcons.Outlined.FastForward,
                    title = stringResource(Res.string.lyrics_offset),
                    subtitle = "${LyricsState.currentLyricsOffsetMs}ms",
                    colorScheme = colorScheme,
                ) {
                    onDismiss()
                    onShowLyricsOffset()
                }
            }

            LyricsToggleRow(
                icon = CrystaListIcons.Outlined.Edit,
                title = stringResource(Res.string.lyrics_respect_agent_positioning),
                subtitle = stringResource(Res.string.follow_agent_positioning_hints),
                checked = respectAgentPositioning,
                colorScheme = colorScheme,
                onCheckedChange = {
                    respectAgentPositioning = it
                    Settings.shared.respectAgentPositioning = it
                },
            )

            LyricsToggleRow(
                icon = CrystaListIcons.Outlined.Edit,
                title = stringResource(Res.string.show_interval_indicator),
                subtitle = stringResource(Res.string.display_gaps_between_sections),
                checked = showIntervalIndicator,
                colorScheme = colorScheme,
                onCheckedChange = { showIntervalIndicator = it },
            )

            LyricsToggleRow(
                icon = CrystaListIcons.Outlined.Translate,
                title = stringResource(Res.string.ai_translation),
                subtitle =
                    if (Settings.shared.lyricsAiActivationMode ==
                        "Manual"
                    ) {
                        stringResource(Res.string.manual_translate_this_track)
                    } else {
                        stringResource(Res.string.automatic_mode)
                    },
                checked = aiTranslationChecked,
                enabled = Settings.shared.lyricsAiTranslationEnabled,
                colorScheme = colorScheme,
                onCheckedChange = {
                    aiTranslationChecked = it
                    LyricsState.setManualLyricsTranslation(it)
                },
            )

            if (LyricsState.currentLyricsEntries.any {
                    !it.translatedText.isNullOrBlank() ||
                        !it.backgroundTranslatedText.isNullOrBlank()
                }
            ) {
                LyricsToggleRow(
                    icon = CrystaListIcons.Outlined.Translate,
                    title = stringResource(Res.string.lyrics_show_translations),
                    checked = showTranslationsChecked,
                    colorScheme = colorScheme,
                    onCheckedChange = {
                        showTranslationsChecked = it
                        Settings.shared.lyricsShowTranslations = it
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
            LyricsToggleRow(
                icon = CrystaListIcons.Outlined.Translate,
                title =
                    stringResource(
                        if (romanizeAsMain) {
                            Res.string.lyrics_romanization_show_original
                        } else {
                            Res.string.romanize_track
                        },
                    ),
                subtitle =
                    stringResource(
                        if (romanizeAsMain) {
                            Res.string.lyrics_romanization_show_original_description
                        } else {
                            Res.string.add_romanization_below_lyrics
                        },
                    ),
                checked = toggleChecked,
                colorScheme = colorScheme,
                onCheckedChange = {
                    if (romanizeAsMain) {
                        Settings.shared.lyricsShowOriginalLyrics = it
                    } else {
                        Settings.shared.lyricsRomanizationEnabled = it
                    }
                },
            )
        }
    }
}

@Composable
private fun LyricsActionChip(
    icon: ImageVector,
    label: String,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurface, maxLines = 1)
        }
    }
}

@Composable
private fun LyricsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.playbackMarquee(),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.playbackMarquee(),
                )
            }
        }
    }
}

@Composable
private fun LyricsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    val contentAlpha = if (enabled) 1f else 0.5f
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary.copy(alpha = contentAlpha),
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = contentAlpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.playbackMarquee(),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.playbackMarquee(),
                )
            }
        }
        ExpressiveSwitch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
fun PlayerMenuPanel(
    expanded: Boolean = true,
    offset: DpOffset = DpOffset.Zero,
    onDismiss: () -> Unit,
    onShowAddToPlaylistDialog: () -> Unit,
    onShowDetailsDialog: (() -> Unit)?,
    onShowSleepTimerDialog: (() -> Unit)?,
    onToggleQueue: (() -> Unit)? = null,
    isQueueActive: Boolean = false,
    onToggleLyrics: (() -> Unit)? = null,
    isLyricsActive: Boolean = false,
    onToggleFullscreen: (() -> Unit)? = null,
    isFullscreen: Boolean = false,
) {
    val currentTrack = PlayerState.currentTrack
    val playback = LocalPlaybackController.current
    var editingSong by remember { mutableStateOf<SongItem?>(null) }
    editingSong?.let { song ->
        SongMetadataEditorDialog(
            song = song,
            onSaved = {
                editingSong = null
                onDismiss()
            },
            onDismiss = { editingSong = null },
        )
    }
    val deleteUploadAction =
        currentTrack?.takeIf { expanded }?.let {
            rememberDeleteUploadAction(it, remember { GlobalContext.get().get<MusicDatabase>() }, onDismiss)
        }
    val artistAction = artistNavigationMenuAction(currentTrack?.artists.orEmpty(), ArtistState::fetchArtistData, onDismiss)
    val listenTogetherManager = ListenTogetherState.listenTogetherManager
    val isInListenTogetherRoom by listenTogetherManager.isInRoom.collectAsState()
    val listenTogetherRole by listenTogetherManager.role.collectAsState()
    val isListenTogetherGuest =
        PlayerState.isListenTogetherGuest() ||
            (isInListenTogetherRoom && listenTogetherRole == ListenTogetherRoomRole.GUEST)
    var showTempoPitch by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }
    if (showEqualizer) {
        EqualizerDialog(
            onDismiss = { showEqualizer = false },
            rememberProfilePicker = { onPicked -> rememberMetroEqProfilePicker(onPicked) },
        )
    }
    val speed by PlayerState.player.playbackSpeed.collectAsState()
    val pitch by PlayerState.player.playbackPitch.collectAsState()
    val varispeed = Settings.shared.varispeed
    PopupVisibility(visible = showTempoPitch) {
        AlertDialog(
            onDismissRequest = { showTempoPitch = false },
            title = { Text(stringResource(if (varispeed) Res.string.playback_speed else Res.string.tempo_and_pitch)) },
            text = {
                TempoPitchControls(
                    speed,
                    pitch,
                    PlayerState::setPlaybackSpeed,
                    PlayerState::setPlaybackPitch,
                    speedEnabled = !isInListenTogetherRoom,
                    varispeed = varispeed,
                    onDone = { showTempoPitch = false },
                )
            },
            confirmButton = {},
        )
    }
    val sleepActive = PlayerState.sleepTimerEnabled
    val sleepTimeLeft = if (expanded && sleepActive) PlayerState.sleepTimerTimeLeftMs.collectAsState().value else 0L
    MediaContextMenu(
        expanded = expanded && editingSong == null,
        offset = offset,
        onDismiss = onDismiss,
        preferSidePlacement = true,
        tonalElevation = 6.dp,
        title = currentTrack?.title ?: stringResource(Res.string.no_track_playing),
        subtitle = currentTrack?.artists?.joinToString(", ") { it.displayName },
        subtitleLinks =
            artistLinksText(currentTrack?.artists.orEmpty()) {
                ArtistState.fetchArtistData(it)
                onDismiss()
            },
        thumbnail = PlayerState.currentArtworkUrl,
        isExplicit = currentTrack?.isExplicit == true,
        primaryActions =
            songPrimaryActions(
                onDismiss = onDismiss,
                isGuest = isListenTogetherGuest,
                playbackEnabled = currentTrack?.isUnavailable == false,
                onStartRadio = currentTrack?.let { track -> { playback.startRadio(track) } },
                onAddToPlaylist =
                    currentTrack?.let {
                        {
                            onShowAddToPlaylistDialog()
                            onDismiss()
                        }
                    },
                onShare =
                    currentTrack?.let { track ->
                        {
                            val url = ShareUrls.song(track.id)
                            AppState.shareLink(url, track.title)
                        }
                    },
            ),
        actionGroups =
            listOf(
                listOfNotNull(
                    artistAction,
                    albumNavigationMenuAction(
                        onDismiss,
                        currentTrack?.album?.id?.let { albumId -> { PlaylistAlbumState.fetchAlbumData(albumId) } },
                        currentTrack?.album?.name,
                    ),
                ),
                listOfNotNull(
                    deleteUploadAction,
                    currentTrack?.let { track ->
                        PlayerMenuAction(
                            CrystaListIcons.Outlined.Edit,
                            stringResource(Res.string.edit),
                            stringResource(Res.string.edit_song_description),
                        ) { editingSong = track }
                    },
                    dislikeMenuAction(
                        currentTrack?.let { PlayerState.isDisliked(it.id) } == true,
                        onDismiss,
                        currentTrack?.let { track -> { PlayerState.toggleDislike(track) } },
                        showDescription = true,
                    ),
                    currentTrack?.artists?.firstOrNull { it.id != null }?.let { artist ->
                        blockArtistMenuAction(onDismiss, { PlayerState.blockArtist(artist) }, artist.displayName)
                    },
                ),
                listOfNotNull(
                    equalizerMenuAction(onOpen = { showEqualizer = true }),
                    tempoPitchMenuAction(varispeed) { showTempoPitch = true },
                    onShowDetailsDialog?.let { action ->
                        PlayerMenuAction(
                            CrystaListIcons.Outlined.Info,
                            stringResource(Res.string.details),
                            stringResource(Res.string.view_song_info),
                        ) {
                            action()
                            onDismiss()
                        }
                    },
                    (onShowSleepTimerDialog != null || sleepActive).takeIf { it && !isListenTogetherGuest }?.let {
                        PlayerMenuAction(
                            CrystaListIcons.Outlined.Bedtime,
                            when {
                                PlayerState.sleepTimerStopAfterCurrentSong -> {
                                    stringResource(Res.string.stopping_after_current_song)
                                }

                                sleepActive -> {
                                    stringResource(Res.string.sleep_label) +
                                        ": ${formatSleepTime(sleepTimeLeft)}"
                                }

                                else -> {
                                    stringResource(Res.string.sleep_timer)
                                }
                            },
                            if (sleepActive) {
                                stringResource(
                                    Res.string.select_to_cancel,
                                )
                            } else {
                                stringResource(Res.string.stop_music_after_set_time)
                            },
                        ) {
                            if (sleepActive) PlayerState.clearSleepTimer() else onShowSleepTimerDialog?.invoke()
                            onDismiss()
                        }
                    },
                    currentTrack?.let { track -> songDownloadAction(track, onDismiss) },
                ),
                listOfNotNull(
                    currentTrack?.takeIf { isListenTogetherGuest }?.let { track ->
                        suggestToHostMenuAction(onDismiss) { ListenTogetherState.suggestTrack(track) }
                    },
                ),
                listOfNotNull(
                    onToggleQueue?.let { toggle ->
                        val queueIcon =
                            if (isQueueActive) {
                                CrystaListIcons.AutoMirrored.Filled.QueueMusic
                            } else {
                                CrystaListIcons.AutoMirrored.Outlined.QueueMusic
                            }
                        val queueLabel =
                            if (isQueueActive) {
                                stringResource(Res.string.hide_queue)
                            } else {
                                stringResource(Res.string.show_queue)
                            }
                        PlayerMenuAction(queueIcon, queueLabel) {
                            toggle()
                            onDismiss()
                        }
                    },
                    onToggleLyrics?.let { toggle ->
                        val lyricsLabel =
                            if (isLyricsActive) {
                                stringResource(Res.string.hide_lyrics)
                            } else {
                                stringResource(Res.string.show_lyrics)
                            }
                        PlayerMenuAction(CrystaListIcons.Filled.Lyrics, lyricsLabel) {
                            toggle()
                            onDismiss()
                        }
                    },
                    onToggleFullscreen?.let { toggle ->
                        val fullscreenIcon =
                            if (isFullscreen) {
                                CrystaListIcons.Outlined.FullscreenExit
                            } else {
                                CrystaListIcons.Outlined.Fullscreen
                            }
                        val fullscreenLabel =
                            if (isFullscreen) {
                                stringResource(Res.string.exit_fullscreen)
                            } else {
                                stringResource(Res.string.fullscreen)
                            }
                        PlayerMenuAction(fullscreenIcon, fullscreenLabel) {
                            toggle()
                            onDismiss()
                        }
                    },
                ),
            ),
    )
}

private fun formatSleepTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

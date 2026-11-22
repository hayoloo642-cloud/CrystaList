/*
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.android.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.crystalist.android.LocalMiniPlayerBottomInset
import com.crystalist.android.playback.LocalPlayerConnection
import com.crystalist.android.ui.screens.rememberDeviceConnectSettingsHost
import com.crystalist.shared.discord.DiscordRpcManager
import com.crystalist.shared.discord.openDiscordUrl
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.copied
import com.crystalist.shared.ui.settings.IntegrationPlaybackState
import com.crystalist.shared.ui.settings.SettingsPageId
import com.crystalist.shared.ui.settings.SettingsPlatform
import com.crystalist.shared.ui.settings.SharedIntegrationsHost
import com.crystalist.shared.ui.settings.SharedIntegrationsScreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun IntegrationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToListenTogether: () -> Unit,
    initialPage: SettingsPageId? = null,
) {
    val copiedMessage = stringResource(Res.string.copied)
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val currentSong by playerConnection?.currentSong?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val duration by playerConnection?.duration?.collectAsState(initial = 0L) ?: remember { mutableLongStateOf(0L) }
    val playbackSpeed by playerConnection?.playbackSpeedFlow?.collectAsState() ?: remember { mutableFloatStateOf(1f) }
    val isPlaying by playerConnection?.isPlaying?.collectAsState(initial = false) ?: remember { mutableStateOf(false) }
    val discordLoginInProgress = DiscordLoginFlow.isActive

    LaunchedEffect(discordLoginInProgress) {
        if (discordLoginInProgress) {
            delay(120_000L)
            if (DiscordLoginFlow.isActive && !DiscordLoginFlow.exchanging) {
                DiscordLoginFlow.clear()
                DiscordRpcManager.setError("discord_error_timeout")
            }
        }
    }

    SharedIntegrationsScreen(
        host =
            SharedIntegrationsHost(
                platform = SettingsPlatform.ANDROID,
                onClose = onNavigateBack,
                bottomContentPadding = LocalMiniPlayerBottomInset.current,
                initialPage = initialPage,
                discordLoginInProgress = discordLoginInProgress,
                playbackPosition = playerConnection?.currentPosition,
                currentPositionMs = { playerConnection?.currentPositionMs() ?: 0L },
                playback =
                    currentSong?.let { song ->
                        IntegrationPlaybackState(
                            song = song,
                            artworkUrl = song.thumbnail,
                            artistArtworkUrl = song.artists.firstOrNull()?.thumbnail,
                            durationMs = duration,
                            isPlaying = isPlaying,
                            playbackSpeed = playbackSpeed,
                        )
                    },
                onStartDiscordLogin = { session ->
                    DiscordLoginFlow.start(session)
                    runCatching { context.openDiscordUrl(session.authorizeUrl) }
                        .onFailure {
                            DiscordLoginFlow.clear()
                            DiscordRpcManager.setError("discord_error_no_browser")
                        }
                },
                deviceConnect = rememberDeviceConnectSettingsHost(),
                onOpenListenTogether = onNavigateToListenTogether,
                onOpenUrl = { url -> runCatching { context.openDiscordUrl(url) } },
                onCopyText = { label, text -> context.copyToClipboard(label, text, copiedMessage) },
                onMessage = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
            ),
    )
}

private fun Context.copyToClipboard(
    label: String,
    text: String,
    copiedMessage: String,
) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(this, copiedMessage, Toast.LENGTH_SHORT).show()
}

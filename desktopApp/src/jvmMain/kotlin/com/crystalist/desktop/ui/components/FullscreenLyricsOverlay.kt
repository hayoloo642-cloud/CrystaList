package com.crystalist.desktop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.focusable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import com.crystalist.desktop.state.app.UiState
import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.shared.ui.components.LiquidBackground
import com.crystalist.shared.ui.components.lyrics.LyricsContrastScrim
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.ui.icons.CrystaListIcons
import com.crystalist.shared.ui.theme.AppleMusic
import com.crystalist.shared.ui.theme.getGradientColors
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.draw.shadow

/**
 * Apple Music style full-screen lyrics.
 *
 * Album-art ambient background, the current line front and centre, and an
 * optional karaoke mode that raises the active line size for singing along.
 */
@Composable
fun FullscreenLyricsOverlay(colorScheme: androidx.compose.material3.ColorScheme) {
    if (!UiState.showFullscreenLyrics) return

    val track = PlayerState.currentTrack ?: return
    val artworkUrl = PlayerState.currentArtworkUrl
    val karaoke = Settings.shared.lyricsKaraokeMode
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.Escape -> {
                            if (Settings.shared.lyricsKaraokeMode) {
                                Settings.shared.lyricsKaraokeMode = false
                            } else {
                                UiState.showFullscreenLyrics = false
                            }
                            true
                        }

                        else -> false
                    }
                },
    ) {
        if (Settings.shared.lyricsAmbientBackground) {
            // Ambient layer: artwork washes the whole window like Apple Music's Now Playing.
            LiquidBackground(
                artworkUrl = artworkUrl,
                modifier = Modifier.fillMaxSize(),
                isLyricsVisible = true,
                animate = true,
                isPlaying = PlayerState.isPlaying,
            )
            LyricsContrastScrim(
                backgroundColors = getGradientColors(MaterialTheme.colorScheme.primary),
                artworkUrl = artworkUrl,
                artworkScrimAlpha = 0.55f,
                isLiquid = true,
                foregroundColor = Color.White,
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left column: artwork with the transport controls beneath, as in Apple Music's
            // full-screen Now Playing.
            Column(
                modifier = Modifier.widthIn(max = 460.dp).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                com.crystalist.shared.ui.components.images.AsyncImage(
                    url = artworkUrl,
                    modifier =
                        Modifier
                            .size(380.dp)
                            .shadow(28.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                )

                Spacer(Modifier.height(20.dp))

                FullscreenTransport()
            }

            Spacer(Modifier.width(56.dp))

            Column(modifier = Modifier.weight(1.35f)) {
                FullscreenLyricsHeader(
                    title = track.title,
                    artists = track.artists.joinToString { it.name },
                    karaoke = karaoke,
                    onToggleKaraoke = {
                        Settings.shared.lyricsKaraokeMode = !Settings.shared.lyricsKaraokeMode
                    },
                    onClose = {
                        Settings.shared.lyricsKaraokeMode = false
                        UiState.showFullscreenLyrics = false
                    },
                )

                Spacer(Modifier.height(8.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    DesktopLyricsContent(
                        textColor = Color.White,
                        // Karaoke enlarges the active line, like Apple Music Sing.
                        textSizeMultiplier = if (karaoke) 1.6f else 1.3f,
                        isVisible = true,
                    )
                }
            }
        }
    }
}

/** Minimal transport row shown under the artwork. */
@Composable
private fun FullscreenTransport() {
    val playing = PlayerState.isPlaying
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { PlayerState.skipPrevious() },
            modifier = Modifier.size(44.dp),
            shapes = androidx.compose.material3.IconButtonDefaults.shapes(shape = CircleShape),
        ) {
            Icon(
                CrystaListIcons.Rounded.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(20.dp))
        IconButton(
            onClick = { PlayerState.togglePlay() },
            modifier = Modifier.size(60.dp),
            shapes = androidx.compose.material3.IconButtonDefaults.shapes(shape = CircleShape),
        ) {
            Icon(
                if (playing) CrystaListIcons.Rounded.Pause else CrystaListIcons.Rounded.PlayArrow,
                contentDescription = if (playing) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.width(20.dp))
        IconButton(
            onClick = { PlayerState.skipNext() },
            modifier = Modifier.size(44.dp),
            shapes = androidx.compose.material3.IconButtonDefaults.shapes(shape = CircleShape),
        ) {
            Icon(
                CrystaListIcons.Rounded.SkipNext,
                contentDescription = "Next",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun FullscreenLyricsHeader(
    title: String,
    artists: String,
    karaoke: Boolean,
    onToggleKaraoke: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (PlayerState.currentArtworkUrl != null) {
                com.crystalist.shared.ui.components.images.AsyncImage(
                    url = PlayerState.currentArtworkUrl,
                    modifier = Modifier.size(52.dp).clipArtwork(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                )
                Spacer(Modifier.width(14.dp))
            }
            Column(modifier = Modifier.widthIn(max = 520.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    artists,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            KaraokeToggle(active = karaoke, onClick = onToggleKaraoke)
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(40.dp),
                shapes = androidx.compose.material3.IconButtonDefaults.shapes(shape = CircleShape),
            ) {
                Icon(
                    CrystaListIcons.Default.Close,
                    contentDescription = "Close lyrics",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

/** Mic pill, mirroring Apple Music Sing's sparkle-mic control. */
@Composable
private fun KaraokeToggle(
    active: Boolean,
    onClick: () -> Unit,
) {
    val glow by animateFloatAsState(if (active) 1f else 0f, label = "karaokeGlow")
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color =
            if (active) {
                AppleMusic.Red
            } else {
                Color.White.copy(alpha = 0.14f)
            },
        contentColor = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                CrystaListIcons.Rounded.Mic,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = Color.White,
            )
            Spacer(Modifier.width(7.dp))
            Text(
                if (active) "Singing" else "Sing",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f + 0.15f * glow),
            )
        }
    }
}

@Composable
private fun FullscreenLyricsFooter(karaoke: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
                if (karaoke) {
                    "Karaoke mode · vocals lowered · Esc to exit"
                } else {
                    "Esc to close · click a line to jump"
                },
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.45f),
        )
    }
}

private fun Modifier.clipArtwork(): Modifier = this.clip(RoundedCornerShape(8.dp))

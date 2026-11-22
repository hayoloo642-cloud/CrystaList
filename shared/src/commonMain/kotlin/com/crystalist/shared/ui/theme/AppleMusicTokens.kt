package com.crystalist.shared.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Apple Music design tokens.
 *
 * Values follow Apple Music's published design language: pure white / true black
 * canvases, `#FA2D48` brand red reserved for playback and selection, iOS system
 * greys for surfaces, and the 8–16dp corner scale.
 */
object AppleMusic {
    // Accent
    val Red = Color(0xFFFA2D48)
    val RedPressed = Color(0xFFD4213B)
    val Coral = Color(0xFFFC3C44)

    // iOS semantic colors
    val SystemBlue = Color(0xFF007AFF)
    val SystemGreen = Color(0xFF34C759)
    val SystemRed = Color(0xFFFF3B30)
    val AtmosGold = Color(0xFFD4A857)
    val LosslessSilver = Color(0xFF8E8E93)

    // Light surfaces
    val LightCanvas = Color(0xFFFFFFFF)
    val LightSurface1 = Color(0xFFF2F2F7)
    val LightSurface2 = Color(0xFFFFFFFF)
    val LightSurface3 = Color(0xFFE5E5EA)
    val LightDivider = Color(0xFFC6C6C8)
    val LightInputFill = Color(0xFFE5E5EA)

    // Dark surfaces
    val DarkCanvas = Color(0xFF000000)
    val DarkSurface1 = Color(0xFF1C1C1E)
    val DarkSurface2 = Color(0xFF2C2C2E)
    val DarkSurface3 = Color(0xFF3A3A3C)
    val DarkDivider = Color(0xFF38383A)
    val DarkInputFill = Color(0xFF1C1C1E)

    // Labels
    val LightLabelSecondary = Color(0x993C3C43) // #3C3C43 at 60%
    val LightLabelTertiary = Color(0x4D3C3C43) // #3C3C43 at 30%
    val DarkLabelSecondary = Color(0x99EBEBF5) // #EBEBF5 at 60%
    val DarkLabelTertiary = Color(0x4DEBEBF5) // #EBEBF5 at 30%

    // Radii — Apple Music softens everything; 12dp on artwork is the signature corner.
    val RadiusSmall = 8.dp
    val RadiusSearch = 10.dp
    val RadiusArtwork = 12.dp
    val RadiusHero = 16.dp

    // Rows
    val TrackRowHeight = 56.dp
    val MiniPlayerHeight = 64.dp
    val SettingsRowHeight = 52.dp
}

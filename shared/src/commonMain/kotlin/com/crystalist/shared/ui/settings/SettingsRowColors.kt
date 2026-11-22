package com.crystalist.shared.ui.settings

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.crystalist.shared.ui.theme.AppleMusic

/**
 * iOS grouped-list rows: white cards on the light grey canvas, `#1C1C1E`
 * cards on the true-black canvas. Pressed rows step up one surface.
 */
fun ColorScheme.settingsRowContainerColor(pressed: Boolean = false): Color {
    val dark = background.luminance() < 0.5f
    return when {
        pressed && dark -> AppleMusic.DarkSurface3
        pressed -> AppleMusic.LightSurface3
        dark -> AppleMusic.DarkSurface1
        else -> AppleMusic.LightCanvas
    }
}

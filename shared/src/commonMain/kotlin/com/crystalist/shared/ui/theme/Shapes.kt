/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crystalist.shared.constants.ThumbnailCornerRadius
import com.crystalist.shared.settings.Settings

// Apple Music corner scale: 8dp small controls, 10dp search, 12dp artwork, 16dp hero.
val CrystaListShapes =
    Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(10.dp),
        large = RoundedCornerShape(12.dp),
        extraLarge = RoundedCornerShape(16.dp),
    )

object ComponentShapes {
    val CardShape = RoundedCornerShape(12.dp)
    val BottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val SmallThumbnailShape get() = RoundedCornerShape(8.dp)
    val ThumbnailShape get() = RoundedCornerShape(12.dp)
    val AlbumArtShape get() = RoundedCornerShape(12.dp)
    val MenuExteriorShape = RoundedCornerShape(12.dp)
    val MenuGroupShape = RoundedCornerShape(10.dp)
    val MenuItemShape = RoundedCornerShape(8.dp)
    val SearchExteriorCornerRadius = 10.dp
    val SearchItemCornerRadius = 8.dp
}

fun <T> uiEnhanced(
    legacy: T,
    enhanced: T,
): T = if (Settings.shared.uiEnhancements) enhanced else legacy

fun gridCellShape(): RoundedCornerShape = RoundedCornerShape(uiEnhanced(ThumbnailCornerRadius, 12.dp))

fun gridCellInset(): Dp = uiEnhanced(1.dp, 4.dp)

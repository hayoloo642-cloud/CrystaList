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

val CrystaListShapes =
    Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(26.dp),
    )

object ComponentShapes {
    val CardShape = RoundedCornerShape(12.dp)
    val BottomSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val SmallThumbnailShape get() = RoundedCornerShape(uiEnhanced(4.dp, 6.dp))
    val ThumbnailShape get() = RoundedCornerShape(uiEnhanced(6.dp, 10.dp))
    val AlbumArtShape get() = RoundedCornerShape(uiEnhanced(10.dp, 16.dp))
    val MenuExteriorShape = RoundedCornerShape(16.dp)
    val MenuGroupShape = RoundedCornerShape(12.dp)
    val MenuItemShape = RoundedCornerShape(8.dp)
    val SearchExteriorCornerRadius = 12.dp
    val SearchItemCornerRadius = 8.dp
}

fun <T> uiEnhanced(
    legacy: T,
    enhanced: T,
): T = if (Settings.shared.uiEnhancements) enhanced else legacy

fun gridCellShape(): RoundedCornerShape = RoundedCornerShape(uiEnhanced(ThumbnailCornerRadius, 12.dp))

fun gridCellInset(): Dp = uiEnhanced(1.dp, 4.dp)

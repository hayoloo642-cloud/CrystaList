/*
 * CrystaList Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Apple Music's signature tint.
val AppleMusicRed = Color(0xFFFA2D48)
val AppleMusicPink = Color(0xFFFC3C44)

// Apple Music light: white canvas, near-black type, system-grey surfaces.
private val AppleLightColors =
    lightColorScheme(
        primary = AppleMusicRed,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFD9DE),
        onPrimaryContainer = Color(0xFF8A0018),
        inversePrimary = Color(0xFFFFB3BE),
        secondary = AppleMusicPink,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFE1E7),
        onSecondaryContainer = Color(0xFF5C0016),
        tertiary = Color(0xFF007AFF),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFDCEBFF),
        onTertiaryContainer = Color(0xFF00305C),
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color(0xFF1C1C1E),
        surfaceVariant = Color(0xFFF2F2F7),
        onSurfaceVariant = Color(0xFF6C6C70),
        surfaceTint = AppleMusicRed,
        inverseSurface = Color(0xFF1C1C1E),
        inverseOnSurface = Color.White,
        error = Color(0xFFFF3B30),
        onError = Color.White,
        errorContainer = Color(0xFFFFE5E3),
        onErrorContainer = Color(0xFF8A0000),
        outline = Color(0xFFC6C6C8),
        outlineVariant = Color(0xFFE5E5EA),
        scrim = Color.Black,
        surfaceBright = Color.White,
        surfaceDim = Color(0xFFE5E5EA),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFF9F9FB),
        surfaceContainer = Color(0xFFF2F2F7),
        surfaceContainerHigh = Color(0xFFEBEBF0),
        surfaceContainerHighest = Color(0xFFE5E5EA),
        primaryFixed = Color(0xFFFFD9DE),
        primaryFixedDim = Color(0xFFFFB3BE),
        onPrimaryFixed = Color(0xFF400009),
        onPrimaryFixedVariant = Color(0xFF8A0018),
        secondaryFixed = Color(0xFFFFE1E7),
        secondaryFixedDim = Color(0xFFFFB1C5),
        onSecondaryFixed = Color(0xFF2E000B),
        onSecondaryFixedVariant = Color(0xFF8A0026),
        tertiaryFixed = Color(0xFFDCEBFF),
        tertiaryFixedDim = Color(0xFFA8CFFF),
        onTertiaryFixed = Color(0xFF001B36),
        onTertiaryFixedVariant = Color(0xFF00305C),
    )

// Apple Music dark: true-black canvas with elevated grey cards.
private val AppleDarkColors =
    darkColorScheme(
        primary = AppleMusicRed,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF3D0710),
        onPrimaryContainer = Color(0xFFFFD9DE),
        inversePrimary = Color(0xFF8A0018),
        secondary = AppleMusicPink,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF3A0A16),
        onSecondaryContainer = Color(0xFFFFE1E7),
        tertiary = Color(0xFF0A84FF),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFF00284D),
        onTertiaryContainer = Color(0xFFDCEBFF),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF1C1C1E),
        onSurfaceVariant = Color(0xFF8E8E93),
        surfaceTint = AppleMusicRed,
        inverseSurface = Color(0xFFF2F2F7),
        inverseOnSurface = Color(0xFF1C1C1E),
        error = Color(0xFFFF453A),
        onError = Color.White,
        errorContainer = Color(0xFF4A0000),
        onErrorContainer = Color(0xFFFFE5E3),
        outline = Color(0xFF38383A),
        outlineVariant = Color(0xFF38383A),
        scrim = Color.Black,
        surfaceBright = Color(0xFF2C2C2E),
        surfaceDim = Color.Black,
        surfaceContainerLowest = Color.Black,
        surfaceContainerLow = Color(0xFF141416),
        surfaceContainer = Color(0xFF1C1C1E),
        surfaceContainerHigh = Color(0xFF2C2C2E),
        surfaceContainerHighest = Color(0xFF3A3A3C),
        primaryFixed = Color(0xFFFFD9DE),
        primaryFixedDim = Color(0xFFFFB3BE),
        onPrimaryFixed = Color(0xFF400009),
        onPrimaryFixedVariant = Color(0xFF8A0018),
        secondaryFixed = Color(0xFFFFE1E7),
        secondaryFixedDim = Color(0xFFFFB1C5),
        onSecondaryFixed = Color(0xFF2E000B),
        onSecondaryFixedVariant = Color(0xFF8A0026),
        tertiaryFixed = Color(0xFFDCEBFF),
        tertiaryFixedDim = Color(0xFFA8CFFF),
        onTertiaryFixed = Color(0xFF001B36),
        onTertiaryFixedVariant = Color(0xFF00305C),
    )

fun appleMusicColorScheme(isDark: Boolean): ColorScheme = if (isDark) AppleDarkColors else AppleLightColors

/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.android.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.crystalist.android.extensions.findActivity
import com.crystalist.android.utils.isAndroidTv
import com.crystalist.shared.ui.LocalIsTv
import com.crystalist.shared.ui.components.FocusOutlineProvider
import com.crystalist.shared.ui.haptics.CrystaListHapticsProvider
import com.crystalist.shared.ui.theme.DefaultThemeColor
import com.crystalist.shared.ui.theme.CrystaListShapes
import com.crystalist.shared.ui.theme.ThemeColorExtractor
import com.crystalist.shared.ui.theme.animateColorScheme
import com.crystalist.shared.ui.theme.pureBlack
import com.crystalist.shared.ui.theme.appleMusicColorScheme
import com.crystalist.shared.ui.theme.crystaListMotionScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrystaListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    dynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val lightStatusBars = SystemBarAppearance.playerLightStatusBars ?: (!darkTheme && SystemBarAppearance.imageOwners == 0)
    SideEffect {
        context.findActivity()?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = lightStatusBars
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    val isTv = remember(context) { context.isAndroidTv() }

    val baseColorScheme = remember(darkTheme) { appleMusicColorScheme(darkTheme) }

    val colorScheme =
        remember(baseColorScheme, pureBlack, darkTheme) {
            if (darkTheme && pureBlack) {
                baseColorScheme.pureBlack()
            } else {
                baseColorScheme
            }
        }

    val motionScheme = remember { crystaListMotionScheme() }
    val animatedColorScheme = animateColorScheme(colorScheme, motionScheme)
    val background = colorScheme.surface.toArgb()
    DisposableEffect(context, background) {
        context.findActivity()?.window?.setBackgroundDrawable(ColorDrawable(background))
        onDispose { }
    }

    MaterialExpressiveTheme(
        colorScheme = animatedColorScheme,
        typography = getAppTypography(),
        shapes = CrystaListShapes,
        motionScheme = motionScheme,
    ) {
        CompositionLocalProvider(LocalIsTv provides isTv) {
            CrystaListHapticsProvider {
                FocusOutlineProvider(enabled = isTv, content = content)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
private fun rememberSystemContrast(context: Context): Float {
    val modeManager = remember(context) { context.getSystemService(UiModeManager::class.java) }
    var contrast by remember(modeManager) { mutableFloatStateOf(modeManager.contrast) }
    DisposableEffect(modeManager) {
        val listener = UiModeManager.ContrastChangeListener { contrast = it }
        modeManager.addContrastChangeListener(context.mainExecutor, listener)
        contrast = modeManager.contrast
        onDispose { modeManager.removeContrastChangeListener(listener) }
    }
    return contrast
}

fun Bitmap.extractThemeColor(): Color = ThemeColorExtractor().extractSeedColor(this)

internal object SystemBarAppearance {
    var imageOwners by mutableIntStateOf(0)
    var playerLightStatusBars by mutableStateOf<Boolean?>(null)
}

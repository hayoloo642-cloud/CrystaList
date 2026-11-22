package com.crystalist.shared.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.crystalist.shared.ui.haptics.CrystaListHapticsProvider

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrystaListTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val motionScheme = remember { crystaListMotionScheme() }
    MaterialExpressiveTheme(
        colorScheme = animateColorScheme(appleMusicColorScheme(useDarkTheme), motionScheme),
        motionScheme = motionScheme,
        typography = CrystaListTypography(),
        shapes = CrystaListShapes,
        content = { CrystaListHapticsProvider(content) },
    )
}

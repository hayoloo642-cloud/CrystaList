package com.crystalist.shared.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme

/**
 * Apple Music keeps transitions smooth and calm rather than springy, so every
 * screen uses the standard motion spec instead of the expressive one.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun crystaListMotionScheme(): MotionScheme = MotionScheme.standard()

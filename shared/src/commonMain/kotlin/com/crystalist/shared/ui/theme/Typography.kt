/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocaleList
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.sp
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.settings.resolveAppLanguageTag
import com.crystalist.shared.state.GlobalYouTube

@Composable
fun currentAppLocaleList(): LocaleList = resolveAppLocaleList(Settings.shared.appLanguage, LocalLocaleList.current)

internal fun resolveAppLocaleList(
    configuredLanguage: String,
    systemLocales: LocaleList,
): LocaleList =
    if (configuredLanguage.isBlank() || configuredLanguage.equals(GlobalYouTube.SYSTEM, ignoreCase = true)) {
        systemLocales
    } else {
        LocaleList(
            resolveAppLanguageTag(configuredLanguage, systemLocales.map { it.toLanguageTag() }),
        )
    }

@Composable
expect fun localeAwareSystemFontFamily(languageTag: String): FontFamily

@Composable
fun CrystaListTypography(localeList: LocaleList = currentAppLocaleList()): Typography {
    val font = localeAwareSystemFontFamily(localeList[0].toLanguageTag())

    return Typography(
        fontFamily = font,
        displayLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 56.sp,
                lineHeight = 60.sp,
                letterSpacing = (-0.6).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 44.sp,
                lineHeight = 48.sp,
                letterSpacing = (-0.5).sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.4).sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.4).sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.3).sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = (-0.2).sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.1).sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 17.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.1).sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Medium,
                localeList = localeList,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.2.sp,
            ),
    )
}

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
        // Apple Music hierarchy: Large Title 34, Title 1 28, Title 2 22, Title 3 20,
        // Headline/Body 17, Subheadline 15, Footnote 13, Caption 12/11.
        displayLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                letterSpacing = 0.37.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.36.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 24.sp,
                lineHeight = 29.sp,
                letterSpacing = (-0.2).sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.36.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Bold,
                localeList = localeList,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.26).sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.15).sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                letterSpacing = (-0.1).sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.43).sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.24).sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 17.sp,
                lineHeight = 25.sp,
                letterSpacing = (-0.43).sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.24).sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                localeList = localeList,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                letterSpacing = (-0.08).sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                localeList = localeList,
                fontSize = 17.sp,
                lineHeight = 17.sp,
                letterSpacing = (-0.43).sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Medium,
                localeList = localeList,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = font,
                fontWeight = FontWeight.Medium,
                localeList = localeList,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                letterSpacing = 0.07.sp,
            ),
    )
}
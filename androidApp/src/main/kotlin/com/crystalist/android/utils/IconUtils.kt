/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.android.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import com.crystalist.android.R
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.app_icon_original
import com.crystalist.shared.settings.Settings
import org.jetbrains.compose.resources.StringResource

private const val MANIFEST_PACKAGE = "com.crystalist.android"
private val LEGACY_ALIAS_NAMES = listOf("MainActivityDefault", "MainActivityDynamic", "MainActivityDynamicBg")

enum class AppIcon(
    val aliasName: String,
    val labelRes: StringResource,
    @DrawableRes val previewRes: Int,
    @DrawableRes val previewBackgroundRes: Int? = null,
    val enabledByDefault: Boolean = false,
) {
    ORIGINAL(
        aliasName = "MainActivityOriginal",
        labelRes = Res.string.app_icon_original,
        previewRes = R.drawable.ic_launcher_foreground,
        previewBackgroundRes = R.drawable.app_icon_original_background,
        enabledByDefault = true,
    ),
    ;

    companion object {
        fun fromPreference(value: String): AppIcon = entries.firstOrNull { it.name == value } ?: ORIGINAL
    }
}

object IconUtils {
    @Synchronized
    fun syncWithPreference(context: Context) = setIcon(context, AppIcon.fromPreference(Settings.shared.appIcon))

    @Synchronized
    fun setIcon(
        context: Context,
        icon: AppIcon,
    ) {
        val pm = context.packageManager
        val selectedComponent = icon.componentName(context)

        if (!pm.isEnabled(selectedComponent, icon.enabledByDefault)) {
            pm.setComponentEnabledSetting(
                selectedComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP,
            )
        }

        LEGACY_ALIAS_NAMES.forEach { aliasName ->
            val component = ComponentName(context, "$MANIFEST_PACKAGE.$aliasName")
            if (pm.getComponentEnabledSetting(component) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                pm.setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                )
            }
        }
    }

    private fun AppIcon.componentName(context: Context) = ComponentName(context, "$MANIFEST_PACKAGE.$aliasName")

    private fun PackageManager.isEnabled(
        component: ComponentName,
        enabledByDefault: Boolean,
    ): Boolean =
        when (getComponentEnabledSetting(component)) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> enabledByDefault
            else -> false
        }
}

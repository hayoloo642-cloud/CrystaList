package com.crystalist.shared.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.app_icon_original
import com.crystalist.shared.settings.Settings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.alternateIconName
import platform.UIKit.setAlternateIconName
import platform.UIKit.supportsAlternateIcons

private enum class IosAppIcon(
    val label: StringResource,
) {
    ORIGINAL(Res.string.app_icon_original),
    ;

    val assetName: String? get() = name.takeUnless { this == ORIGINAL }
}

private object IosAppIconSelection {
    private var changing = false

    fun sync() {
        val actualName = UIApplication.sharedApplication.alternateIconName
        IosAppIcon.entries.firstOrNull { it.assetName == actualName }?.let {
            Settings.shared.appIcon = it.name
        }
    }

    fun select(
        name: String,
        onError: () -> Unit,
    ) {
        val icon = IosAppIcon.entries.firstOrNull { it.name == name } ?: return
        val application = UIApplication.sharedApplication
        if (changing || !application.supportsAlternateIcons) return
        if (application.alternateIconName == icon.assetName) {
            sync()
            return
        }
        changing = true
        application.setAlternateIconName(icon.assetName) { error ->
            // UIKit may invoke completion away from the main thread.
            NSOperationQueue.mainQueue.addOperationWithBlock {
                changing = false
                sync()
                if (error != null) onError()
            }
        }
    }
}

@Composable
actual fun rememberIosAppIconActions(onError: () -> Unit): IosAppIconActions {
    val latestError = rememberUpdatedState(onError)
    val supported = remember { UIApplication.sharedApplication.supportsAlternateIcons }
    DisposableEffect(Unit) {
        IosAppIconSelection.sync()
        val observer =
            NSNotificationCenter.defaultCenter.addObserverForName(
                UIApplicationDidBecomeActiveNotification,
                null,
                NSOperationQueue.mainQueue,
            ) { IosAppIconSelection.sync() }
        onDispose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }
    val choices =
        if (supported) {
            IosAppIcon.entries.map { SettingsChoice(it.name, stringResource(it.label)) }
        } else {
            emptyList()
        }
    return IosAppIconActions(choices) { IosAppIconSelection.select(it) { latestError.value() } }
}

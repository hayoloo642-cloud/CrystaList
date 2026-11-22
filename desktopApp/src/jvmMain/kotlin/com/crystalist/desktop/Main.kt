/*
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:Suppress("FunctionName")

@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)

package com.crystalist.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.setSingletonImageLoaderFactory
import com.materialkolor.ktx.isLight
import com.materialkolor.utils.ColorUtils
import com.crystalist.desktop.backup.DesktopAutomaticBackupScheduler
import com.crystalist.desktop.backup.DesktopBackupRestoreManager
import com.crystalist.desktop.crash.DesktopCrashRecovery
import com.crystalist.desktop.deviceconnect.DesktopDeviceConnect
import com.crystalist.desktop.di.composeStateModule
import com.crystalist.desktop.input.DesktopShortcutLayer
import com.crystalist.desktop.input.desktopScrollConfig
import com.crystalist.desktop.state.DesktopLocaleManager
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.state.app.DesktopNavigation
import com.crystalist.desktop.state.app.UiState
import com.crystalist.desktop.state.auth.AuthState
import com.crystalist.desktop.state.content.HomeState
import com.crystalist.desktop.state.content.LibraryContentState
import com.crystalist.desktop.state.content.LibraryState
import com.crystalist.desktop.state.playback.DesktopPlaybackController
import com.crystalist.desktop.state.playback.ListenTogetherState
import com.crystalist.desktop.state.playback.LyricsState
import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.desktop.storage.DownloadManager
import com.crystalist.desktop.storage.DownloadState
import com.crystalist.desktop.storage.SongCacheManager
import com.crystalist.desktop.ui.DesktopDensityProvider
import com.crystalist.desktop.ui.components.DesktopMenuBar
import com.crystalist.desktop.ui.components.DesktopPlayerVisibility
import com.crystalist.desktop.ui.components.FloatingSearchOverlay
import com.crystalist.desktop.ui.components.FullscreenPlayerView
import com.crystalist.desktop.ui.components.LocalCaptionBarInsets
import com.crystalist.desktop.ui.components.MiniplayerWindow
import com.crystalist.desktop.ui.components.ShortcutsOverlay
import com.crystalist.desktop.ui.components.TokenLoginDialog
import com.crystalist.desktop.ui.components.UpdateArtworkTheme
import com.crystalist.desktop.ui.components.VideoFullscreenOverlay
import com.crystalist.desktop.ui.layouts.ExpressLayout
import com.crystalist.desktop.ui.layouts.FocusedLayout
import com.crystalist.desktop.ui.layouts.LayoutUtils
import com.crystalist.desktop.ui.screens.DesktopOnboarding
import com.crystalist.desktop.ui.screens.LastFmArtistBrowser
import com.crystalist.desktop.ui.screens.LastFmArtistBrowserHost
import com.crystalist.desktop.ui.screens.LoginDialog
import com.crystalist.desktop.ui.screens.settings.ApplicationLogsDialog
import com.crystalist.desktop.ui.screens.settings.IntegrationsOverlay
import com.crystalist.desktop.ui.screens.settings.SettingsOverlay
import com.crystalist.desktop.ui.theme.CrystaListTheme
import com.crystalist.desktop.utils.AppLogger
import com.crystalist.desktop.utils.ComposeTrayManager
import com.crystalist.desktop.utils.DesktopImageLoader
import com.crystalist.desktop.utils.DesktopNativeCrashRecovery
import com.crystalist.desktop.utils.DesktopSingleInstance
import com.crystalist.desktop.utils.DesktopStartup
import com.crystalist.desktop.utils.DesktopUrlHandler
import com.crystalist.desktop.utils.MatugenColors
import com.crystalist.desktop.utils.NativeMemoryTrimmer
import com.crystalist.desktop.utils.STOP_OMARCHY_URL
import com.crystalist.desktop.utils.SentryManager
import com.crystalist.desktop.utils.SkiaCacheCleaner
import com.crystalist.desktop.utils.SystemMediaManager
import com.crystalist.desktop.utils.ThemeUtils
import com.crystalist.desktop.utils.WaylandScale
import com.crystalist.desktop.utils.WindowsAppIdentity
import com.crystalist.desktop.utils.configureDesktopJvmErrorFile
import com.crystalist.desktop.utils.configureLinuxWebKitRenderer
import com.crystalist.desktop.utils.desktopRenderApiOverride
import com.crystalist.desktop.utils.isDesktopPlatformSupported
import com.crystalist.desktop.utils.isOmarchy
import com.crystalist.desktop.utils.openStopOmarchy
import com.crystalist.desktop.web.DesktopWebViewError
import com.crystalist.desktop.window.IconResources
import com.crystalist.desktop.window.LinuxWindowFrame
import com.crystalist.desktop.window.WindowFrame
import com.crystalist.desktop.window.WindowVisibility
import com.crystalist.desktop.window.findSkiaLayer
import com.crystalist.desktop.window.restoreWindowPosition
import com.crystalist.desktop.window.setWindowPlacement
import com.crystalist.desktop.youtube.DesktopPageBoundTokenHost
import com.crystalist.desktop.youtube.DesktopPageBoundTokenRuntime
import com.crystalist.shared.BuildConfig
import com.crystalist.shared.api.lastfm.LastFM
import com.crystalist.shared.auth.YouTubeSessionState
import com.crystalist.shared.db.DatabaseDowngradeException
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.discord.DiscordRpcManager
import com.crystalist.shared.discord.observeDiscordInitialization
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.action_login
import com.crystalist.shared.generated.resources.action_not_now
import com.crystalist.shared.generated.resources.app_name
import com.crystalist.shared.generated.resources.close
import com.crystalist.shared.generated.resources.copy
import com.crystalist.shared.generated.resources.desktop_display_unavailable
import com.crystalist.shared.generated.resources.desktop_omarchy_unsupported
import com.crystalist.shared.generated.resources.desktop_platform_unsupported
import com.crystalist.shared.generated.resources.lastfm_session_expired_description
import com.crystalist.shared.generated.resources.lastfm_session_expired_title
import com.crystalist.shared.generated.resources.learn_more
import com.crystalist.shared.generated.resources.liked_songs
import com.crystalist.shared.generated.resources.ok
import com.crystalist.shared.generated.resources.playback_error
import com.crystalist.shared.generated.resources.technical_information
import com.crystalist.shared.generated.resources.window_title_now_playing
import com.crystalist.shared.generated.resources.youtube_session_expired_message
import com.crystalist.shared.generated.resources.youtube_session_expired_title
import com.crystalist.shared.scrobble.ScrobbleSessions
import com.crystalist.shared.settings.AppLayout
import com.crystalist.shared.settings.LibraryFilterType
import com.crystalist.shared.settings.PlayerBackgroundStyle
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.settings.defaultOpenTabRoot
import com.crystalist.shared.settings.defaultOpenTabScreen
import com.crystalist.shared.settings.desktopDefaultOpenTab
import com.crystalist.shared.settings.displayName
import com.crystalist.shared.settings.flushDesktopCredentialWarnings
import com.crystalist.shared.settings.flushDesktopSettings
import com.crystalist.shared.settings.recoverDesktopCredentials
import com.crystalist.shared.settings.refreshRecoveredDesktopCredentials
import com.crystalist.shared.state.GlobalInnerTube
import com.crystalist.shared.state.GlobalYouTube
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.state.SongLikeState
import com.crystalist.shared.state.SongMetadataState
import com.crystalist.shared.state.applyContentRegion
import com.crystalist.shared.state.coreModule
import com.crystalist.shared.ui.YouTubeSyncSnackbar
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.HomeLoadingPlaceholder
import com.crystalist.shared.ui.components.LoadingScreen
import com.crystalist.shared.ui.components.LocalDownloadedSongIds
import com.crystalist.shared.ui.components.LocalVisualWorkActive
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.navigation.LocalNavigationContainerState
import com.crystalist.shared.ui.navigation.LocalNavigator
import com.crystalist.shared.ui.navigation.NavigationContentProvider
import com.crystalist.shared.ui.navigation.Navigator
import com.crystalist.shared.ui.navigation.Screen
import com.crystalist.shared.ui.navigation.rememberNavigationState
import com.crystalist.shared.ui.playback.LocalPlaybackController
import com.crystalist.shared.ui.screens.SupportPrompt
import com.crystalist.shared.ui.settings.SettingsPageId
import com.crystalist.shared.ui.utils.LocalMouseScrollFactors
import com.crystalist.shared.ui.utils.desktopMouseScrollFactors
import com.crystalist.shared.ui.utils.softwareRendering
import com.crystalist.shared.ui.utils.verticalScrollWithDrag
import com.crystalist.shared.utils.OsUtil
import com.crystalist.shared.utils.XdgUtils
import com.crystalist.shared.utils.isCrashReportingAvailable
import com.crystalist.shared.youtube.di.sharedModule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skiko.GraphicsApi
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import sun.misc.Signal
import sun.misc.SignalHandler
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.desktop.AppReopenedListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess
import java.awt.Color as AwtColor

@Composable
fun WindowScope.App(
    navigator: Navigator,
    onMaximized: () -> Unit,
    toggleWindowFullscreen: () -> Unit,
    checkDarkMode: (Boolean) -> Unit,
    isLinux: Boolean = false,
) {
    val playbackController = remember { DesktopPlaybackController(AppState.scope) }
    val downloadStates = LibraryState.downloadStates
    val downloadedSongIds = remember(downloadStates) { downloadStates.filterValues { it == DownloadState.DOWNLOADED }.keys }
    NavigationContentProvider(
        LocalNavigator provides navigator,
        LocalNavigationContainerState provides navigator.state.containers,
        LocalPlaybackController provides playbackController,
        LocalDownloadedSongIds provides downloadedSongIds,
        LocalMouseScrollFactors provides desktopMouseScrollFactors(LocalDensity.current),
        desktopScrollConfig(),
    ) {
        AppContent(navigator, onMaximized, toggleWindowFullscreen, checkDarkMode, isLinux)
    }
}

@Composable
private fun WindowScope.AppContent(
    navigator: Navigator,
    onMaximized: () -> Unit,
    toggleWindowFullscreen: () -> Unit,
    checkDarkMode: (Boolean) -> Unit,
    isLinux: Boolean,
) {
    CrystaListTheme {
        val colorScheme = MaterialTheme.colorScheme
        WindowsWindowBackground(colorScheme.background)
        val isExpandedPlayerVisible = PlayerState.currentTrack != null && PlayerState.isExpanded
        checkDarkMode(
            if (isExpandedPlayerVisible && !UiState.showSettings && !UiState.isRefreshingApp) {
                Settings.shared.playerBackgroundStyle != PlayerBackgroundStyle.SOLID || !colorScheme.surfaceContainer.isLight()
            } else {
                !colorScheme.background.isLight()
            },
        )
        var onboardingComplete by remember { mutableStateOf(Settings.shared.desktopOnboardingComplete) }
        if (!onboardingComplete) {
            DesktopOnboarding { sendCrashReports, openLogin ->
                if (isCrashReportingAvailable()) {
                    if (Settings.shared.desktopPendingOnboardingSentryConsent.isNotBlank()) {
                        Settings.shared.desktopPendingOnboardingSentryConsent = sendCrashReports.toString()
                    }
                    Settings.shared.sentryEnabled = sendCrashReports
                }
                Settings.shared.desktopOnboardingComplete = true
                applicationScope.launch(Dispatchers.IO) { SentryManager.initialize() }
                if (openLogin) {
                    UiState.showSignIn = true
                    UiState.showSwitchChannel = false
                }
                onboardingComplete = true
            }
            return@CrystaListTheme
        }
        val rootFocusRequester = remember { FocusRequester() }
        val contentFocusRequester = remember { FocusRequester() }
        var isRootFocused by remember { mutableStateOf(false) }
        var isPlayerBackgroundFocused by remember { mutableStateOf(false) }
        val lastFmBrowser = remember { LastFmArtistBrowser().also { LastFM.renderedPageLoader = it::load } }
        setSingletonImageLoaderFactory { context ->
            DesktopImageLoader.create(context)
        }
        UpdateArtworkTheme()
        SupportPrompt()
        val matugenEnabled = Settings.shared.matugenTheme
        LaunchedEffect(matugenEnabled) {
            if (matugenEnabled) MatugenColors.watch()
        }
        LaunchedEffect(colorScheme) {
            try {
                val bg = colorScheme.background.toArgb()
                val fg = colorScheme.onSurface.toArgb()
                val border = colorScheme.outline.toArgb()
                val dark = colorScheme.background.luminance() < 0.5f
                ComposeTrayManager.setThemeColors(bg, fg, border, dark)
                if (!isLinux) {
                    window.background = colorScheme.background.toAwtColor()
                }
            } catch (_: Exception) {
            }
        }
        var searchWasOpen by remember { mutableStateOf(false) }
        LaunchedEffect(UiState.showFloatingSearch) {
            if (UiState.showFloatingSearch) {
                searchWasOpen = true
            } else if (searchWasOpen) {
                contentFocusRequester.requestFocus()
                searchWasOpen = false
            }
        }
        LaunchedEffect(isExpandedPlayerVisible) {
            if (!isExpandedPlayerVisible && !UiState.showFloatingSearch) rootFocusRequester.requestFocus()
        }
        val windowCornerRadius =
            if (OsUtil.isMac && !UiState.isMaximized) {
                10.dp
            } else {
                0.dp
            }
        val outlineColor =
            if (!UiState.isMaximized) {
                if (isLinux) {
                    if (UiState.isWindowFocused) {
                        colorScheme.outlineVariant
                    } else {
                        colorScheme.outlineVariant.copy(
                            alpha = 0.5f,
                        )
                    }
                } else {
                    if (UiState.isWindowFocused) colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                }
            } else {
                Color.Transparent
            }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .focusRequester(contentFocusRequester)
                    .focusRestorer(rootFocusRequester)
                    .focusGroup()
                    .clip(RoundedCornerShape(windowCornerRadius)),
        ) {
            LastFmArtistBrowserHost(lastFmBrowser)
            Box(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(LocalCaptionBarInsets.current.only(WindowInsetsSides.Top))
                    .focusRequester(rootFocusRequester)
                    .onFocusChanged { isRootFocused = it.isFocused }
                    .focusable()
                    .pointerInput(rootFocusRequester) {
                        detectTapGestures { rootFocusRequester.requestFocus() }
                    },
            )
            DesktopPageBoundTokenHost(GlobalContext.get().get<DesktopPageBoundTokenRuntime>())
            DesktopWebViewError()
            if (UiState.showIntegrationsSettings) {
                IntegrationsOverlay(colorScheme)
            }
            PopupVisibility(visible = UiState.showLogViewer) {
                ApplicationLogsDialog(onDismiss = { UiState.showLogViewer = false })
            }
            PopupVisibility(visible = UiState.showTokenLogin) {
                TokenLoginDialog(
                    onDismiss = { UiState.showTokenLogin = false },
                    onSaved = {
                        UiState.showTokenLogin = false
                        UiState.appRefreshKey++
                    },
                )
            }
            if (UiState.showSignIn || UiState.showSwitchChannel) {
                val isSwitchingChannel = UiState.showSwitchChannel
                LoginDialog(
                    onDismissRequest = {
                        UiState.showSignIn = false
                        UiState.showSwitchChannel = false
                    },
                    onAuthDataExtracted = { cookie, visitorData, dataSyncId, authUser ->
                        AuthState.updateAuth(
                            cookie,
                            visitorData,
                            dataSyncId,
                            authUser,
                            accountInfo = GlobalYouTubeRepository.instance.lastAccountInfo,
                        )
                        UiState.showSignIn = false
                        UiState.showSwitchChannel = false
                        UiState.appRefreshKey++
                    },
                    isSwitchingChannel = isSwitchingChannel,
                )
            }
            PopupVisibility(visible = YouTubeSessionState.showExpiredSessionNotice) {
                AlertDialog(
                    onDismissRequest = YouTubeSessionState::dismissExpiredSessionNotice,
                    title = { Text(stringResource(Res.string.youtube_session_expired_title)) },
                    text = {
                        Text(
                            stringResource(Res.string.youtube_session_expired_message),
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                YouTubeSessionState.dismissExpiredSessionNotice()
                                UiState.showSignIn = true
                                UiState.showSwitchChannel = false
                            },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(stringResource(Res.string.action_login))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = YouTubeSessionState::dismissExpiredSessionNotice, shapes = ButtonDefaults.shapes()) {
                            Text(stringResource(Res.string.action_not_now))
                        }
                    },
                )
            }
            PopupVisibility(visible = ScrobbleSessions.showLastFmExpiredNotice && !YouTubeSessionState.showExpiredSessionNotice) {
                AlertDialog(
                    onDismissRequest = ScrobbleSessions::dismissLastFmExpiredNotice,
                    title = { Text(stringResource(Res.string.lastfm_session_expired_title)) },
                    text = { Text(stringResource(Res.string.lastfm_session_expired_description)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                ScrobbleSessions.dismissLastFmExpiredNotice()
                                UiState.integrationsInitialPage = SettingsPageId.LAST_FM
                                UiState.showIntegrationsSettings = true
                            },
                            shapes = ButtonDefaults.shapes(),
                        ) {
                            Text(stringResource(Res.string.action_login))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = ScrobbleSessions::dismissLastFmExpiredNotice, shapes = ButtonDefaults.shapes()) {
                            Text(stringResource(Res.string.action_not_now))
                        }
                    },
                )
            }
            PopupVisibility(visible = PlayerState.showPlayerError) {
                AlertDialog(
                    onDismissRequest = { PlayerState.showPlayerError = false },
                    title = { Text(stringResource(Res.string.playback_error)) },
                    text = {
                        val details = PlayerState.playerErrorDetails
                        Column(Modifier.heightIn(max = 360.dp).verticalScrollWithDrag(rememberScrollState())) {
                            Text(PlayerState.playerErrorMessage)
                            if (details.isNotBlank()) {
                                Text(
                                    stringResource(Res.string.technical_information),
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                                )
                                SelectionContainer {
                                    Text(details, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { PlayerState.showPlayerError = false }, shapes = ButtonDefaults.shapes()) {
                            Text(stringResource(Res.string.ok))
                        }
                    },
                    dismissButton =
                        PlayerState.playerErrorDetails.takeIf { it.isNotBlank() }?.let { details ->
                            {
                                TextButton(
                                    onClick = { AppState.copyToClipboard("${PlayerState.playerErrorMessage}\n\n$details") },
                                    shapes = ButtonDefaults.shapes(),
                                ) {
                                    Text(stringResource(Res.string.copy))
                                }
                            }
                        },
                )
            }
            DesktopShortcutLayer(
                clearPointerFocus = { rootFocusRequester.requestFocus() },
                onForward = { navigator.goForward() },
                modifier = Modifier.fillMaxSize(),
                isPlaybackBackgroundFocused = {
                    isRootFocused || (isExpandedPlayerVisible && isPlayerBackgroundFocused)
                },
                onRefresh = { LayoutUtils.refreshCurrentPage(navigator.state.currentRoute) },
                toggleWindowFullscreen = toggleWindowFullscreen,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(windowCornerRadius))
                            .background(colorScheme.background),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .border(
                                        if (UiState.isMaximized || Settings.shared.enableSystemWindowFrame) 0.dp else 1.dp,
                                        outlineColor,
                                        RoundedCornerShape(windowCornerRadius),
                                    ),
                        ) {
                            key(UiState.appRefreshKey) {
                                when (Settings.shared.appLayout) {
                                    AppLayout.COMFORTABLE,
                                    AppLayout.EXPRESS,
                                    -> ExpressLayout(onMaximized, toggleWindowFullscreen) { rootFocusRequester.requestFocus() }

                                    AppLayout.DYNAMIC,
                                    AppLayout.FOCUSED,
                                    -> FocusedLayout(onMaximized, toggleWindowFullscreen) { rootFocusRequester.requestFocus() }
                                }
                            }
                        }
                    }
                }
                DesktopPlayerVisibility(visible = isExpandedPlayerVisible) {
                    FullscreenPlayerView(
                        seedColor = UiState.seedColor,
                        toggleFullscreen = { toggleWindowFullscreen() },
                        onBackgroundFocusChanged = { isPlayerBackgroundFocused = it },
                        onFocus = {
                            window.requestFocus()
                        },
                    )
                }
            }
            VideoFullscreenOverlay(colorScheme, toggleWindowFullscreen)
            if (UiState.isRefreshingApp) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.surface.copy(alpha = 0.94f),
                ) {
                    LoadingScreen()
                }
            }
            PopupVisibility(visible = UiState.showSettings) {
                SettingsOverlay(colorScheme)
            }
            YouTubeSyncSnackbar(AppState.snackbarHostState)
            val snackbarScheme = rememberUpdatedState(colorScheme)
            SnackbarHost(AppState.snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)) { data ->
                val scheme = snackbarScheme.value
                Snackbar(
                    snackbarData = data,
                    containerColor = scheme.inverseSurface,
                    contentColor = scheme.inverseOnSurface,
                    actionColor = scheme.inversePrimary,
                    actionContentColor = scheme.inversePrimary,
                    dismissActionContentColor = scheme.inverseOnSurface,
                )
            }
        }

        if (UiState.showFloatingSearch) {
            FloatingSearchOverlay(
                colorScheme = colorScheme,
                onDismiss = { UiState.showFloatingSearch = false },
            )
        }

        PopupVisibility(visible = UiState.showShortcutsDialog) {
            ShortcutsOverlay(
                colorScheme = colorScheme,
                onDismiss = { UiState.showShortcutsDialog = false },
            )
        }
    }
}

private fun Color.toAwtColor(): AwtColor = AwtColor(red, green, blue, alpha)

@Composable
private fun WindowScope.WindowsWindowBackground(color: Color) {
    if (!OsUtil.isWindows) return
    val window = window as? ComposeWindow ?: return
    SideEffect {
        val background = color.toAwtColor()
        window.background = background
        window.contentPane.background = background
        window.findSkiaLayer()?.let { layer ->
            layer.background = background
            layer.canvas.background = background
        }
    }
}

private val isShuttingDown = AtomicBoolean(false)

// Swing's main dispatcher initializes AWT, so create it only after display scale setup.
private val applicationScope by lazy { CoroutineScope(Dispatchers.Main + SupervisorJob()) }
private val shutdownScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
private var uiVisible by mutableStateOf(true)
private val uiDisposed = CompletableDeferred<Unit>()
private val startup = DesktopStartup()
private var startupJob: Job? = null
private const val UI_DISPOSE_TIMEOUT_MS = 500L

private fun startupRuntimeAvailable(): Boolean {
    var probe: Path? = null
    return try {
        probe = Files.createTempFile("crystalist-startup-", ".tmp")
        Files.write(probe, ByteArray(4096))
        Class.forName("org.sqlite.JDBC")
        DriverManager.getConnection("jdbc:sqlite::memory:").use { }
        true
    } catch (error: Throwable) {
        if (error !is Exception && error !is LinkageError) throw error
        val location = System.getProperty("java.io.tmpdir")
        val message =
            "CrystaList cannot initialize its native runtime or temporary storage.\n\n" +
                "Location: $location\n" +
                "Error: ${error.message ?: error::class.simpleName}\n\n" +
                "Free disk space or adjust the temporary directory, then restart CrystaList."
        System.err.println(message)
        error.printStackTrace(System.err)
        if (!GraphicsEnvironment.isHeadless()) {
            runCatching {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    "CrystaList startup error",
                    JOptionPane.ERROR_MESSAGE,
                )
            }
        }
        false
    } finally {
        probe?.let { runCatching { Files.deleteIfExists(it) } }
    }
}

private fun configureLinuxWindowIdentity() {
    if (!OsUtil.isLinux) return
    System.setProperty("awt.app.id", "CrystaList")
    runCatching {
        // XToolkit's constructor names the X11 class after the creating thread, so create it here and then rename it.
        Toolkit.getDefaultToolkit()
        val xToolkit = Class.forName("sun.awt.X11.XToolkit")
        xToolkit.getDeclaredField("awtAppClassName").apply {
            isAccessible = true
            set(null, "CrystaList")
        }
    }
}

private fun shutdownApp(onComplete: () -> Unit) {
    if (!isShuttingDown.compareAndSet(false, true)) return
    shutdownScope.launch {
        try {
            runCatching { DiscordRpcManager.destroy() }
            // Bounded: after a fatal error the composition may never dispose, and cleanup must still run.
            withTimeoutOrNull(UI_DISPOSE_TIMEOUT_MS) {
                withContext(Dispatchers.Main) { uiVisible = false }
                uiDisposed.await()
            }
            startupJob?.cancelAndJoin()
            runCatching { applicationScope.coroutineContext[Job]?.cancelAndJoin() }
            if (startup.initialized) {
                val deviceConnectRelease = launch { runCatching { DesktopDeviceConnect.manager.release() } }
                runCatching { PlayerState.release() }
                runCatching { ComposeTrayManager.cleanup() }
                runCatching { SongCacheManager.release() }
                runCatching { DownloadManager.release() }
                runCatching { SystemMediaManager.shutdown() }
                runCatching { AppState.release() }
                runCatching { PlayerState.player.release() }
                runCatching { DesktopImageLoader.release() }
                runCatching { SentryManager.shutdown() }
                deviceConnectRelease.join()
            }
            runCatching { stopKoin() }
            runCatching { flushDesktopSettings() }.onFailure { it.printStackTrace() }
            DesktopNativeCrashRecovery.cleanExit()
            runCatching { AppLogger.shutdown() }
        } finally {
            DesktopSingleInstance.release()
            EventQueue.invokeLater(onComplete)
        }
    }
}

internal fun runBoundedShutdown(
    timeoutMillis: Long = 2_000L,
    beginShutdown: ((() -> Unit) -> Unit),
    terminate: () -> Unit,
    forceTerminate: () -> Unit,
) {
    val isTerminating = AtomicBoolean(false)
    val terminateOnce = { if (isTerminating.compareAndSet(false, true)) terminate() }
    Thread({
        runCatching { Thread.sleep(timeoutMillis) }
        forceTerminate()
    }, "ShutdownWatchdog").apply {
        priority = Thread.MAX_PRIORITY
        start()
    }
    Thread({
        try {
            beginShutdown(terminateOnce)
        } catch (_: Throwable) {
            terminateOnce()
        }
    }, "ShutdownCleanup").apply {
        priority = Thread.MAX_PRIORITY
        start()
    }
}

private fun installGracefulShutdownSignals() {
    Runtime.getRuntime().addShutdownHook(
        Thread({ runBlocking { DiscordRpcManager.destroy() } }, "DiscordPresenceShutdown"),
    )
    val handler =
        SignalHandler { signal ->
            AppLogger.info("Main", "Received ${signal.name} signal, shutting down")
            runBoundedShutdown(
                beginShutdown = ::shutdownApp,
                terminate = { exitProcess(0) },
                forceTerminate = { Runtime.getRuntime().halt(0) },
            )
        }
    runCatching { Signal.handle(Signal("TERM"), handler) }
    runCatching { Signal.handle(Signal("INT"), handler) }
}

fun main() {
    main(emptyArray())
}

fun main(args: Array<String>) {
    // The JDK reads this once when its default proxy selector initializes.
    if (OsUtil.isWindows) System.getProperties().putIfAbsent("java.net.useSystemProxies", "true")
    runCatching { System.getProperty("crystalist.cds.cache")?.let { File(it).mkdirs() } }
    startup.trace("main")
    val recoveryIndex = args.indexOf(DesktopCrashRecovery.ARGUMENT)
    val pendingReport = if (recoveryIndex < 0) runCatching { DesktopCrashRecovery.store.pendingReportId() }.getOrNull() else null
    if (recoveryIndex >= 0 || pendingReport != null) {
        DesktopCrashRecovery.show(pendingReport ?: args.getOrElse(recoveryIndex + 1) { "" })
        return
    }
    DesktopCrashRecovery.install()
    try {
        configureDesktopJvmErrorFile()
        NativeMemoryTrimmer.limitArenasByReexec()
        runDesktopApp(args)
    } catch (error: Throwable) {
        Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(Thread.currentThread(), error)
    }
}

private fun exitWithStartupMessage(message: String): Nothing {
    System.err.println(message)
    if (!GraphicsEnvironment.isHeadless()) {
        runCatching {
            JOptionPane.showMessageDialog(null, message, runBlocking { getString(Res.string.app_name) }, JOptionPane.ERROR_MESSAGE)
        }
    }
    DesktopSingleInstance.release()
    exitProcess(1)
}

private fun exitOnOmarchy(): Nothing {
    val message = runBlocking { getString(Res.string.desktop_omarchy_unsupported) }
    System.err.println("$message $STOP_OMARCHY_URL")
    if (!GraphicsEnvironment.isHeadless()) {
        runCatching {
            val learnMore = runBlocking { getString(Res.string.learn_more) }
            val choice =
                JOptionPane.showOptionDialog(
                    null,
                    message,
                    runBlocking { getString(Res.string.app_name) },
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    arrayOf(learnMore, runBlocking { getString(Res.string.close) }),
                    learnMore,
                )
            if (choice == 0) openStopOmarchy()
        }
    }
    DesktopSingleInstance.release()
    exitProcess(1)
}

private fun runDesktopApp(args: Array<String>) {
    if (OsUtil.isLinux && isOmarchy()) exitOnOmarchy()
    if (!isDesktopPlatformSupported()) {
        exitWithStartupMessage(runBlocking { getString(Res.string.desktop_platform_unsupported) })
    }
    val isLinux = OsUtil.isLinux
    if (OsUtil.isWindows) {
        WindowsAppIdentity.setProcessIdentity()
    }
    if (isLinux) {
        configureLinuxWebKitRenderer()
        if (!WaylandScale.ensureDisplay()) {
            System.err.println(runBlocking { getString(Res.string.desktop_display_unavailable) })
            exitProcess(1)
        }
    }
    System.setProperty("org.bytedeco.javacpp.maxPhysicalBytes", "0")
    System.setProperty("org.bytedeco.javacpp.maxBytes", "0")
    if (!DesktopSingleInstance.acquire(args)) return
    installGracefulShutdownSignals()
    DesktopBackupRestoreManager
        .applyPendingRestore(Settings.shared)
    val settings = Settings.shared
    desktopRenderApiOverride(
        choice = settings.desktopRenderBackend,
        osName = OsUtil.name,
        osArch = System.getProperty("os.arch"),
        environment = System.getenv("SKIKO_RENDER_API"),
        property = System.getProperty("skiko.renderApi"),
    )?.let { System.setProperty("skiko.renderApi", it) }
    if (OsUtil.isMac) {
        System.setProperty("apple.awt.enableTemplateImages", "true")
        // Without this AppKit keeps the Aqua (light) title bar even in Dark Mode.
        System.setProperty("apple.awt.application.appearance", "system")
        if (System.getProperty("skiko.renderApi") == null && System.getenv("SKIKO_RENDER_API") == null) {
            System.setProperty("skiko.renderApi", "METAL")
        }
        DesktopUrlHandler.install()
    }
    args.firstOrNull { DesktopUrlHandler.canHandle(it) }?.let { url ->
        DesktopUrlHandler.handleUrl(url)
    }
    if (!settings.desktopOnboardingInitialized) {
        settings.desktopOnboardingComplete = File(XdgUtils.getXdgDataHome(), "crystalist/crystalist.db").isFile
        settings.desktopOnboardingInitialized = true
    }
    if (isCrashReportingAvailable()) {
        settings.desktopPendingOnboardingSentryConsent.takeIf(String::isNotBlank)?.let { consent ->
            settings.sentryEnabled = consent.toBooleanStrict()
            settings.desktopPendingOnboardingSentryConsent = ""
        }
    }
    DesktopLocaleManager.apply(settings.appLanguage)

    if (isLinux) {
        println("[Main] Linux detected, applying display scale")
        WaylandScale.detectAndApply()
        configureLinuxWindowIdentity()
    }
    DesktopSingleInstance.startRestoreWatcher()

    // Initialize logging only after settings migration to avoid java.util.prefs/JUL class-init deadlocks.
    AppLogger.initialize(applicationScope)
    AppLogger.setEnabled(settings.enableLogging)
    flushDesktopCredentialWarnings()
    startup.trace("settings")

    AppLogger.info("Main", "CrystaList starting up")
    AppLogger.info("Main", "OS: ${OsUtil.name}")
    val javaVersion = Runtime.version()
    AppLogger.info(
        "Main",
        "Java Version: feature=${javaVersion.feature()} interim=${javaVersion.interim()} " +
            "update=${javaVersion.update()} patch=${javaVersion.patch()}",
    )
    AppLogger.info("Main", "App Version: ${BuildConfig.APP_VERSION}")
    if (settings.enableLogging && System.getProperty("skiko.hardwareInfo.enabled") == null) {
        System.setProperty("skiko.hardwareInfo.enabled", "true")
    }

    val windowsIdentity =
        if (OsUtil.isWindows) {
            applicationScope.launch(Dispatchers.IO) {
                startup.firstFrame.await()
                startup.windowShown.await()
                WindowsAppIdentity.register(getString(Res.string.app_name))
            }
        } else {
            null
        }
    val systemJvmLocale = DesktopLocaleManager.systemLocale
    var backendReady by mutableStateOf(false)

    suspend fun initializeBackend() {
        startup.initialize {
            if (!startupRuntimeAvailable()) {
                DesktopSingleInstance.release()
                exitProcess(1)
            }
            startup.trace("runtime")
            if (settings.enableLogging) {
                val runtime = ManagementFactory.getRuntimeMXBean()
                AppLogger.info(
                    "Runtime",
                    "vendor=${runtime.vmVendor} version=${runtime.vmVersion} arch=${System.getProperty("os.arch")} " +
                        "mode=${System.getProperty("java.vm.info")}",
                )
                val diagnosticOptions =
                    runtime.inputArguments.filter { option ->
                        option.startsWith("-XX:TieredStopAtLevel=") || option.startsWith("-XX:CICompilerCount=") ||
                            option.startsWith("-XX:ReservedCodeCacheSize=") || option.startsWith("-Xshare:") ||
                            option.startsWith("-XX:SharedArchiveFile=") || option == "-XX:+AutoCreateSharedArchive"
                    }
                AppLogger.info("Runtime", "JIT/CDS options: ${diagnosticOptions.joinToString(" ")}")
                AppLogger.info(
                    "Runtime",
                    "Skiko request: env=${System.getenv("SKIKO_RENDER_API")} property=${System.getProperty("skiko.renderApi")} " +
                        "angle=${System.getProperty("skiko.rendering.angle.enabled")}",
                )
            }
            DesktopNativeCrashRecovery.start()
            startKoin {
                modules(
                    coreModule,
                    sharedModule,
                    composeStateModule,
                )
            }
            val database =
                try {
                    GlobalContext.get().get<MusicDatabase>()
                } catch (error: Exception) {
                    val downgrade =
                        generateSequence<Throwable>(error) { it.cause }.filterIsInstance<DatabaseDowngradeException>().firstOrNull()
                            ?: throw error
                    exitWithStartupMessage(downgrade.message.orEmpty())
                }
            startup.trace("database")
            runBlocking { SongLikeState.recoverDatabaseLikes(database) }
            DesktopAutomaticBackupScheduler.start(applicationScope)
            SongMetadataState.bind(database, applicationScope)

            LastFM.initialize(
                apiKey = BuildConfig.LASTFM_API_KEY,
                secret = BuildConfig.LASTFM_SECRET,
            )

            LastFM.sessionKey = settings.lastFmSession.ifBlank { null }
            ScrobbleSessions
                .restorePersistedAccounts()

            GlobalYouTube.applyContentRegion(
                contentCountry = settings.contentCountry,
                contentLanguage = settings.contentLanguage,
                systemLocale = systemJvmLocale,
            )

            // Singleton construction scans storage and creates clients; keep it off the EDT.
            SongCacheManager.cacheDir
            PlayerState.player
            LibraryState.downloadStates
            // Create these snapshot states before the full UI composes: if a background coroutine creates them while a
            // composition reads them for the first time, Compose rejects states newer than its snapshot and crashes.
            SongLikeState.likedSongIds
            LyricsState.currentLyricsEntries
            HomeState.setLikedSongsName(runBlocking { getString(Res.string.liked_songs) })
            ListenTogetherState.initialize()
            if (OsUtil.isWindows) {
                listOf("previous.ico", "play.ico", "pause.ico", "next.ico").forEach { IconResources.toTempFile(it) }
            }
        }

        fun fetchAnonymousVisitorData() {
            applicationScope.launchVisitorDataFetch(
                shouldFetch = !settings.credentialsPending && settings.innerTubeCookie.isBlank() && settings.visitorData.isBlank(),
                fetch = {
                    AppLogger.info("Main", "Fetching visitor data for unauthenticated user...")
                    GlobalInnerTube.instance.fetchFreshVisitorData()
                },
                save = saveVisitorData@{
                    if (settings.credentialsPending || settings.innerTubeCookie.isNotBlank() ||
                        settings.visitorData.isNotBlank()
                    ) {
                        return@saveVisitorData
                    }
                    settings.visitorData = it
                    GlobalYouTube.updateSession(
                        cookie = settings.innerTubeCookie,
                        visitorData = it,
                        dataSyncId = settings.dataSyncId,
                        authUser = settings.innerTubeAuthUser,
                        useLoginForBrowse = false,
                    )
                    AppLogger.info("Main", "Visitor data fetched")
                },
                onBlank = { AppLogger.warning("Main", "Visitor data fetch returned null/blank") },
                onFailure = { AppLogger.warning("Main", "Visitor data fetch failed", it) },
            )
        }
        fetchAnonymousVisitorData()

        AuthState.loadSession()
        if (isLinux || OsUtil.isMac) {
            applicationScope.launch(Dispatchers.IO) {
                var retryDelayMs = 1_000L
                while (true) {
                    when (recoverDesktopCredentials()) {
                        null -> {
                            if (!isLinux) break
                            retryDelayMs = 1_000L
                        }

                        true -> {
                            withContext(Dispatchers.Main) {
                                refreshRecoveredDesktopCredentials()
                                ScrobbleSessions.restorePersistedAccounts()
                                if (settings.enableDiscordIntegration) {
                                    DiscordRpcManager.init(GlobalYouTube.client)
                                }
                                settings.discordAccessToken.takeIf { it.isNotBlank() }?.let(DiscordRpcManager::reconnectWithToken)
                                GlobalYouTube.applyContentRegion(
                                    contentCountry = settings.contentCountry,
                                    contentLanguage = settings.contentLanguage,
                                    systemLocale = systemJvmLocale,
                                )
                                AuthState.loadSession()
                                fetchAnonymousVisitorData()
                            }
                            applicationScope.launch(Dispatchers.Default) { DesktopDeviceConnect.manager.restoreIfEnabled(Unit) }
                            retryDelayMs = 1_000L
                        }

                        false -> {
                            retryDelayMs = (retryDelayMs * 2).coerceAtMost(30_000L)
                        }
                    }
                    delay(retryDelayMs)
                }
            }
        }
        applicationScope.launch(Dispatchers.Default) {
            if (!settings.credentialsPending) DesktopDeviceConnect.manager.restoreIfEnabled(Unit)
        }

        backendReady = true
        startup.appFrame.await()
        applicationScope.launch(Dispatchers.IO) {
            if (settings.desktopOnboardingComplete) SentryManager.initialize()
            SentryManager.reportPlaybackClientStats()
        }
        applicationScope.launch {
            observeDiscordInitialization { DiscordRpcManager.init(GlobalYouTube.client) }
        }
        SkiaCacheCleaner.initialize()
        NativeMemoryTrimmer.initialize()
    }

    if (WaylandScale.isWayland() && !Settings.shared.waylandDefaultFrameApplied) {
        Settings.shared.enableSystemWindowFrame = true
        Settings.shared.waylandDefaultFrameApplied = true
    }
    if (OsUtil.isMac && !Settings.shared.macDefaultFrameApplied) {
        Settings.shared.enableSystemWindowFrame = true
        Settings.shared.macDefaultFrameApplied = true
    }
    val useSystemWindowFrame = Settings.shared.enableSystemWindowFrame

    applicationScope.launch(Dispatchers.IO) { ThemeUtils.observeSystemTheme() }
    startupJob = applicationScope.launch { initializeBackend() }

    application {
        if (!uiVisible) {
            SideEffect { uiDisposed.complete(Unit) }
            LaunchedEffect(Unit) { awaitCancellation() }
            return@application
        }
        var integrationsReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            startup.appFrame.await()
            integrationsReady = true
        }
        val savedWidth = Settings.shared.savedWidth
        val savedHeight = Settings.shared.savedHeight

        val windowPlacement =
            remember {
                WindowPlacement.entries.firstOrNull { it.name == Settings.shared.desktopWindowPlacement } ?: WindowPlacement.Floating
            }
        val savedPosition =
            remember {
                restoreWindowPosition(
                    Settings.shared.desktopWindowPosition,
                    GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.map { it.defaultConfiguration.bounds },
                )
            }

        val windowState =
            rememberWindowState(
                placement = windowPlacement,
                position = savedPosition?.let { WindowPosition(it.x.dp, it.y.dp) } ?: WindowPosition(Alignment.Center),
                size = DpSize(savedWidth.coerceAtLeast(800).dp, savedHeight.coerceAtLeast(500).dp),
            )

        var lastFloatingSize by remember { mutableStateOf(windowState.size) }
        var lastFloatingPosition by remember { mutableStateOf(windowState.position) }
        SideEffect {
            if (windowState.placement == WindowPlacement.Floating) {
                lastFloatingSize = windowState.size
                lastFloatingPosition = windowState.position
            }
        }
        DisposableEffect(windowState) {
            onDispose {
                Settings.shared.savedWidth = lastFloatingSize.width.value.toInt()
                Settings.shared.savedHeight = lastFloatingSize.height.value.toInt()
                (lastFloatingPosition as? WindowPosition.Absolute)?.let {
                    Settings.shared.desktopWindowPosition = "${it.x.value.toInt()},${it.y.value.toInt()}"
                }
                Settings.shared.desktopWindowPlacement = windowState.placement.name
            }
        }

        val lastNormalPlacement =
            remember {
                mutableStateOf(windowPlacement.takeUnless { it == WindowPlacement.Fullscreen } ?: WindowPlacement.Floating)
            }

        LaunchedEffect(windowState.placement) {
            if (windowState.placement != WindowPlacement.Fullscreen) {
                lastNormalPlacement.value = windowState.placement
            }
            UiState.isMaximized =
                windowState.placement == WindowPlacement.Maximized || windowState.placement == WindowPlacement.Fullscreen
            UiState.isNativeFullscreen = windowState.placement == WindowPlacement.Fullscreen
        }

        @Suppress("DEPRECATION")
        val logoPainter = painterResource("logo.svg")
        val iconColor =
            UiState.seedColor

        val dynamicIcon =
            remember(iconColor) {
                object : Painter() {
                    override val intrinsicSize: Size = Size(256f, 256f)

                    override fun DrawScope.onDraw() {
                        drawCircle(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            iconColor.copy(alpha = 0.8f),
                                            iconColor,
                                            iconColor.copy(alpha = 0.6f),
                                        ),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, size.height),
                                ),
                            radius = size.minDimension / 2,
                            center = center,
                        )

                        drawCircle(
                            color = iconColor,
                            radius = size.minDimension / 2.5f,
                            center = center,
                        )

                        with(logoPainter) {
                            val logoSize = size * 0.75f
                            val offset =
                                Offset((size.width - logoSize.width) / 2f, (size.height - logoSize.height) / 2f)
                            translate(offset.x, offset.y) {
                                draw(
                                    logoSize,
                                    colorFilter =
                                        ColorFilter
                                            .tint(
                                                color =
                                                    if (ColorUtils.calculateLuminance(iconColor.toArgb()) > 0.4) {
                                                        Color(
                                                            red = (iconColor.red / 255) * (32 / 255),
                                                            green = (iconColor.green / 255) * (32 / 255),
                                                            blue = (iconColor.blue / 255) * (32 / 255),
                                                        )
                                                    } else {
                                                        Color.White
                                                    },
                                            ),
                                )
                            }
                        }
                    }
                }
            }

        val startRoute =
            remember {
                when (defaultOpenTabRoot(Settings.shared.desktopDefaultOpenTab())) {
                    "SEARCH" -> {
                        Screen.Search
                    }

                    "LIBRARY" -> {
                        Settings.shared.libraryFilter = LibraryFilterType.DEFAULT
                        Screen.Library
                    }

                    else -> {
                        Screen.Home
                    }
                }
            }
        val topLevelRoutes =
            remember {
                setOf(
                    Screen.Home,
                    Screen.Search,
                    Screen.Library,
                    Screen.History,
                    Screen.Statistics,
                    Screen.ListenTogether,
                )
            }
        val navigationState =
            rememberNavigationState(
                startRoute,
                topLevelRoutes,
                initialDestination = remember { defaultOpenTabScreen(Settings.shared.desktopDefaultOpenTab()) },
            )
        val navigator = remember(navigationState) { Navigator(navigationState) }
        DisposableEffect(navigator) {
            DesktopNavigation.bind(navigator)
            onDispose { DesktopNavigation.unbind(navigator) }
        }

        fun minimizeToTrayOrClose() {
            if (Settings.shared.minimizeToTrayOnClose) {
                ComposeTrayManager.hideWindow()
            } else {
                shutdownApp { exitProcess(0) }
            }
        }

        val appTitle = if (BuildConfig.DEBUG) "CrystaList Debug" else "CrystaList"
        val windowTitle =
            PlayerState.currentTrack?.takeIf { it.title.isNotBlank() }?.let { track ->
                val artists = track.artists.joinToString { it.displayName }
                if (artists.isBlank()) track.title else stringResource(Res.string.window_title_now_playing, track.title, artists)
            } ?: appTitle
        Window(
            onCloseRequest = { minimizeToTrayOrClose() },
            title = windowTitle,
            state = windowState,
            undecorated = isLinux && !useSystemWindowFrame,
            transparent = false,
            icon = dynamicIcon,
        ) {
            LaunchedEffect(window) {
                while (!window.isShowing) delay(16L)
                startup.windowShown.complete(Unit)
                startup.firstFrame.await()
                val renderApi = window.findSkiaLayer()?.renderApi
                softwareRendering = renderApi == GraphicsApi.SOFTWARE_FAST || renderApi == GraphicsApi.SOFTWARE_COMPAT
                AppLogger.info(
                    "Main",
                    "Skiko render API: requested=${System.getenv("SKIKO_RENDER_API") ?: System.getProperty("skiko.renderApi") ?: "AUTO"} " +
                        "actual=$renderApi",
                )
            }
            if (!backendReady) {
                DesktopDensityProvider {
                    CrystaListTheme(seedColor = UiState.seedColor) {
                        WindowsWindowBackground(MaterialTheme.colorScheme.background)
                        val shell: @Composable () -> Unit = {
                            Surface(
                                modifier =
                                    Modifier.fillMaxSize().drawWithContent {
                                        drawContent()
                                        startup.frameDrawn(backendReady = false)
                                    },
                            ) {
                                Column(Modifier.padding(24.dp)) {
                                    Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.titleLarge)
                                    HomeLoadingPlaceholder(Modifier.fillMaxSize().padding(top = 24.dp))
                                }
                            }
                        }
                        if (isLinux) {
                            LinuxWindowFrame(
                                onCloseRequest = { minimizeToTrayOrClose() },
                                darkTheme = !MaterialTheme.colorScheme.background.isLight(),
                                state = windowState,
                            ) { shell() }
                        } else {
                            shell()
                        }
                    }
                }
                return@Window
            }
            LaunchedEffect(window) {
                runCatching {
                    val icon =
                        withContext(Dispatchers.IO) {
                            Thread
                                .currentThread()
                                .contextClassLoader
                                .getResourceAsStream("logo.png")
                                ?.use(ImageIO::read)
                        }
                    if (icon != null) (window as? Frame)?.iconImage = icon
                }
            }

            if (OsUtil.isMac) {
                DesktopMenuBar(
                    navigator = navigator,
                    windowState = windowState,
                    toggleWindowFullscreen = {
                        val placement =
                            if (windowState.placement == WindowPlacement.Fullscreen) {
                                lastNormalPlacement.value
                            } else {
                                WindowPlacement.Fullscreen
                            }
                        setWindowPlacement(windowState, placement) { window.placement = it }
                    },
                    onRefresh = {
                        UiState.isRefreshingApp = true
                        LibraryContentState.fetchLibraryData(force = true)
                        UiState.appRefreshKey++
                        HomeState.fetchHomeData {
                            UiState.isRefreshingApp = false
                        }
                    },
                    onCloseWindow = { minimizeToTrayOrClose() },
                    onQuit = { shutdownApp { exitProcess(0) } },
                )
            }

            LaunchedEffect(Unit) {
                try {
                    window.minimumSize = java.awt.Dimension(800, 500)
                } catch (_: Exception) {
                }
            }

            DisposableEffect(useSystemWindowFrame) {
                if (OsUtil.isMac && !useSystemWindowFrame) {
                    try {
                        val rootPane = window.rootPane
                        rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                        rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                        rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
                        rootPane.putClientProperty("apple.awt.draggableWindowBackground", false)
                    } catch (_: Exception) {
                    }
                }
                onDispose { }
            }

            val placementBeforeMinimize = remember { mutableStateOf<WindowPlacement?>(null) }

            LaunchedEffect(windowState.isMinimized) {
                if (windowState.isMinimized) {
                    placementBeforeMinimize.value = windowState.placement
                } else {
                    placementBeforeMinimize.value?.let { saved ->
                        windowState.placement = saved
                        placementBeforeMinimize.value = null
                    }
                }
            }

            DisposableEffect(window) {
                UiState.isWindowFocused = window.isFocused
                var preMinimizePlacement: WindowPlacement? = null

                val listener =
                    object : WindowAdapter() {
                        override fun windowActivated(e: WindowEvent?) {
                            UiState.isWindowFocused = true
                        }

                        override fun windowDeactivated(e: WindowEvent?) {
                            UiState.isWindowFocused = false
                        }

                        override fun windowIconified(e: WindowEvent?) {
                            preMinimizePlacement = windowState.placement
                            ComposeTrayManager.setWindowVisible(false)
                        }

                        override fun windowDeiconified(e: WindowEvent?) {
                            ComposeTrayManager.setWindowVisible(true)
                            preMinimizePlacement?.let { targetPlacement ->
                                SwingUtilities.invokeLater {
                                    windowState.placement = targetPlacement
                                }
                            }
                        }
                    }
                window.addWindowListener(listener)

                // AppKit changes fullscreen outside Compose; defer until windowState.placement catches up.
                val windowStateListener =
                    java.awt.event.WindowStateListener { _ ->
                        if (OsUtil.isMac) {
                            SwingUtilities.invokeLater {
                                val isFs = windowState.placement == WindowPlacement.Fullscreen
                                UiState.isNativeFullscreen = isFs
                                UiState.isMaximized = windowState.placement == WindowPlacement.Maximized ||
                                    windowState.placement == WindowPlacement.Fullscreen
                            }
                        }
                    }
                window.addWindowStateListener(windowStateListener)

                var preFullscreenSize: java.awt.Dimension? = null
                var preFullscreenLocation: java.awt.Point? = null

                val componentListener =
                    object : java.awt.event.ComponentAdapter() {
                        override fun componentResized(e: java.awt.event.ComponentEvent?) {
                            if (OsUtil.isMac) {
                                val isFs = windowState.placement == WindowPlacement.Fullscreen
                                if (!isFs) {
                                    preFullscreenSize = window.size
                                    preFullscreenLocation = window.location
                                }
                                UiState.isNativeFullscreen = isFs
                            }
                        }

                        override fun componentMoved(e: java.awt.event.ComponentEvent?) {
                            if (OsUtil.isMac) {
                                if (windowState.placement != WindowPlacement.Fullscreen) {
                                    preFullscreenLocation = window.location
                                }
                                if (!useSystemWindowFrame && windowState.placement == WindowPlacement.Floating &&
                                    !UiState.isMaximized
                                ) {
                                    try {
                                        val config = window.graphicsConfiguration ?: return
                                        val insets =
                                            java.awt.Toolkit
                                                .getDefaultToolkit()
                                                .getScreenInsets(config)
                                        val minTopY = insets.top
                                        if (window.y < minTopY) {
                                            window.setLocation(window.x, minTopY)
                                        }
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                    }
                window.addComponentListener(componentListener)

                onDispose {
                    window.removeWindowListener(listener)
                    window.removeWindowStateListener(windowStateListener)
                    window.removeComponentListener(componentListener)
                }
            }

            LaunchedEffect(window) {
                startup.appFrame.await()
                windowsIdentity?.join()
                SystemMediaManager.onQuit = {
                    AppLogger.info("Main", "Media controls requested quit, shutting down")
                    shutdownApp { exitProcess(0) }
                }
                SystemMediaManager.initialize()
                SystemMediaManager.onRaise = ComposeTrayManager::showWindow
            }

            LaunchedEffect(window) {
                ComposeTrayManager.initialize(windowState, window) {
                    shutdownApp { exitProcess(0) }
                }
                DesktopSingleInstance.setRestoreHandler { payload ->
                    ComposeTrayManager.showWindow()
                    if (DesktopUrlHandler.canHandle(payload)) {
                        DesktopUrlHandler.handleUrl(payload)
                    }
                }
                startup.appFrame.await()
                if (!OsUtil.isMac) withContext(Dispatchers.IO) { DesktopUrlHandler.install() }
                DesktopUrlHandler.onUiReady()
            }

            if (OsUtil.isMac) {
                DisposableEffect(Unit) {
                    val reopenListener =
                        AppReopenedListener {
                            ComposeTrayManager.showWindow()
                        }
                    runCatching {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().addAppEventListener(reopenListener)
                        }
                    }
                    onDispose {
                        runCatching {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().removeAppEventListener(reopenListener)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(UiState.restartRequested) {
                if (UiState.restartRequested) {
                    AppLogger.info("Main", "Restarting application for setting change")
                    shutdownApp {
                        DesktopBackupRestoreManager
                            .restart()
                            .getOrElse { exitProcess(1) }
                    }
                }
            }

            LaunchedEffect(UiState.updateManagerStarted) {
                if (UiState.updateManagerStarted) {
                    AppLogger.info("Main", "Update manager started, shutting down")
                    shutdownApp { exitProcess(0) }
                }
            }

            fun onClose() {
                if (Settings.shared.minimizeToTrayOnClose) {
                    ComposeTrayManager.hideWindow()
                } else {
                    AppLogger.info("Main", "Application shutting down")
                    shutdownApp { exitProcess(0) }
                }
            }

            val areWeInDarkMode = remember { mutableStateOf(false) }

            DesktopDensityProvider {
                Box(
                    Modifier.fillMaxSize().drawWithContent {
                        drawContent()
                        startup.frameDrawn(backendReady = true)
                    },
                ) {
                    WindowVisibility(window) {
                        val mainWindowVisibility = LocalVisualWorkActive.current
                        LaunchedEffect(mainWindowVisibility) {
                            try {
                                mainWindowVisibility.collect(PlayerState::setVideoWindowVisible)
                            } finally {
                                PlayerState.setVideoWindowVisible(false)
                            }
                        }
                        WindowFrame(
                            onCloseRequest = { onClose() },
                            lastNormalPlacement = lastNormalPlacement.value,
                            darkTheme = areWeInDarkMode.value,
                            state = windowState,
                        ) { _, contentInset, onMaximized, toggleFullscreen ->
                            CompositionLocalProvider(LocalCaptionBarInsets provides contentInset) {
                                App(
                                    navigator = navigator,
                                    onMaximized = { onMaximized() },
                                    toggleWindowFullscreen = toggleFullscreen,
                                    checkDarkMode = { areWeInDarkMode.value = it },
                                    isLinux = isLinux,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (integrationsReady && Settings.shared.desktopOnboardingComplete) {
            MiniplayerWindow()
        }

        // System tray icon (stock Compose Tray on macOS, ComposeNativeTray on Linux/Windows)
        if (integrationsReady) {
            with(ComposeTrayManager) {
                TrayIcon()
            }
        }
    }
}

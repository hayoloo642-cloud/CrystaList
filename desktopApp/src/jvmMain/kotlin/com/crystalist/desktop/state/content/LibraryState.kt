/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.desktop.state.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.crystalist.desktop.state.app.AppState
import com.crystalist.desktop.state.app.DesktopNavigation
import com.crystalist.desktop.storage.DownloadManager
import com.crystalist.desktop.storage.DownloadedFile
import com.crystalist.shared.settings.LibraryViewType
import com.crystalist.shared.settings.NavItem
import com.crystalist.shared.settings.Settings
import com.crystalist.shared.settings.desktopDefaultOpenTab
import com.crystalist.shared.settings.desktopSidebarNavItems
import com.crystalist.shared.settings.updateDefaultOpenTab
import com.crystalist.shared.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object LibraryState {
    var downloadStates by mutableStateOf(DownloadManager.downloadStates.value)
    var downloadProgress by mutableStateOf(DownloadManager.downloadProgress.value)
    var downloadedFiles by mutableStateOf<List<DownloadedFile>>(DownloadManager.downloadedFiles.value)
        private set
    var isDownloadManagerLoaded by mutableStateOf(false)
    var sessionCompletedCount by mutableStateOf(DownloadManager.sessionCompletedCount.value)
    var downloadTitles by mutableStateOf(DownloadManager.downloadTitles.value)

    init {
        AppState.scope.launch {
            DownloadManager.downloadStates.collectLatest { states ->
                downloadStates = states
            }
        }
        AppState.scope.launch {
            DownloadManager.downloadProgress.collectLatest { progress ->
                downloadProgress = progress
            }
        }
        AppState.scope.launch {
            DownloadManager.downloadTitles.collectLatest { titles ->
                downloadTitles = titles
            }
        }
        AppState.scope.launch {
            DownloadManager.sessionCompletedCount.collectLatest { count ->
                sessionCompletedCount = count
            }
        }
        AppState.scope.launch {
            DownloadManager.downloadedFiles.collectLatest { files ->
                downloadedFiles = files
                isDownloadManagerLoaded = true
            }
        }
    }

    var libraryViewType by mutableStateOf(Settings.shared.libraryViewType)
        private set

    fun updateLibraryViewType(type: LibraryViewType) {
        libraryViewType = type
        Settings.shared.libraryViewType = type
    }

    var sidebarNavItems by mutableStateOf(Settings.shared.desktopSidebarNavItems())

    var isEditingSidebar by mutableStateOf(false)

    fun saveSidebarNavItems(items: List<NavItem>) {
        sidebarNavItems = items
        val stringValue = items.joinToString(",") { "${it.id}:${it.visible}" }
        Settings.shared.sidebarNavItems = stringValue
        Settings.shared.updateDefaultOpenTab(Settings.shared.desktopDefaultOpenTab())
    }

    fun navigateToAutoPlaylist(type: String) {
        DesktopNavigation.navigate(Screen.AutoPlaylist(type))
    }
}

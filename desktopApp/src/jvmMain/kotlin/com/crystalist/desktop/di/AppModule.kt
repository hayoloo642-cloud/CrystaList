/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.desktop.di

import com.crystalist.desktop.state.playback.PlayerState
import com.crystalist.desktop.storage.DownloadManager
import com.crystalist.desktop.storage.SongCacheManager
import com.crystalist.desktop.storage.desktopSongOfflineAvailability
import com.crystalist.desktop.youtube.DesktopPageBoundTokenRuntime
import com.metrolist.innertubex.extraction.TokenProvider
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.OfflineSongAvailability
import com.crystalist.shared.db.daos.DatabaseDao
import com.crystalist.shared.db.getDatabaseBuilder
import com.crystalist.shared.youtube.playback.DesktopTokenProvider
import com.crystalist.shared.youtube.playback.LocalAudioSource
import org.koin.dsl.module

val composeStateModule =
    module {
        single<MusicDatabase> { getDatabaseBuilder().build() }
        single<LocalAudioSource> {
            LocalAudioSource { id, song ->
                DownloadManager.getLocalPlaybackResource(id, song) ?: SongCacheManager.getCachedPlaybackResource(id, song)
            }
        }
        single<DatabaseDao> { get<MusicDatabase>().dao() }
        single<OfflineSongAvailability> { desktopSongOfflineAvailability(get()) }
        single { DesktopPageBoundTokenRuntime() }
        single<TokenProvider> {
            DesktopTokenProvider(get<DesktopPageBoundTokenRuntime>()) {
                val queue = PlayerState.queue.snapshots.value
                queue.displayedItems
                    .asSequence()
                    .drop(queue.currentIndex + 1)
                    .take(2)
                    .map { it.id }
                    .toList()
            }
        }
    }

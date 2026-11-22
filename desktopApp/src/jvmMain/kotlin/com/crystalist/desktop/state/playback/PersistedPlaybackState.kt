package com.crystalist.desktop.state.playback

import com.crystalist.shared.model.RepeatMode
import com.crystalist.shared.playback.MusicQueueSnapshot
import com.crystalist.shared.playback.StoredQueueSnapshotSerializer
import kotlinx.serialization.Serializable

@Serializable
data class PersistedPlaybackState(
    @Serializable(with = StoredQueueSnapshotSerializer::class)
    val queue: MusicQueueSnapshot,
    val positionMs: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val revision: String? = null,
)

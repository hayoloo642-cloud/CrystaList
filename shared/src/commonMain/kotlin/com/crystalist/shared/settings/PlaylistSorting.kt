package com.crystalist.shared.settings

import com.crystalist.shared.model.CollectionSort
import com.crystalist.shared.model.PlaylistSongSort
import com.crystalist.shared.model.SortDirection
import kotlinx.serialization.Serializable

@Serializable
data class SavedPlaylistSongSorting(
    val sort: PlaylistSongSort,
    val direction: SortDirection,
)

@Serializable
data class SavedPlaylistCollectionSorting(
    val sort: CollectionSort,
    val direction: SortDirection,
)

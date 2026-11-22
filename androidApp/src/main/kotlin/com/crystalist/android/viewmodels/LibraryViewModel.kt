package com.crystalist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.model.PlaylistItem
import com.crystalist.shared.playlist.createStoredLocalPlaylist
import com.crystalist.shared.playlist.createStoredPlaylist
import com.crystalist.shared.playlist.deleteStoredPlaylist
import com.crystalist.shared.playlist.renameStoredPlaylist
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.viewmodels.LibraryViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val database: MusicDatabase,
) : ViewModel() {
    private val sharedViewModel =
        LibraryViewModel(viewModelScope, database)
    val uiState = sharedViewModel.uiState
    val presentation = sharedViewModel.presentation
    val contentMatchedSongs = sharedViewModel.contentMatchedSongs
    val contentSearchHasMore = sharedViewModel.contentSearchHasMore

    fun loadMoreContentMatches() = sharedViewModel.loadMoreContentMatches()

    fun setSearchDescending(descending: Boolean) = sharedViewModel.setSearchDescending(descending)

    fun setSearchQuery(query: String) = sharedViewModel.setSearchQuery(query)

    fun setActive(active: Boolean) = sharedViewModel.setActive(active)

    fun refresh(force: Boolean = true) = sharedViewModel.refresh(force)

    fun refreshAfterHomeLoaded() = sharedViewModel.refreshAfterHomeLoaded()

    fun createPlaylist(
        name: String,
        local: Boolean,
        onResult: (Result<String>) -> Unit,
    ) {
        viewModelScope.launch {
            val result =
                if (local) {
                    createStoredLocalPlaylist(database, name).map { it.id }
                } else {
                    val repository = GlobalYouTubeRepository.instance
                    if (repository.canManagePlaylists()) {
                        createStoredPlaylist(database, repository, name)
                    } else {
                        Result.failure(IllegalStateException())
                    }
                }
            if (result.isSuccess) refresh()
            onResult(result)
        }
    }

    fun renamePlaylist(
        playlist: PlaylistItem,
        name: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = renameStoredPlaylist(database, GlobalYouTubeRepository.instance, playlist.id, name)
            if (result.isSuccess) refresh()
            onResult(result)
        }
    }

    fun deletePlaylist(
        playlist: PlaylistItem,
        onResult: (Result<Unit>) -> Unit,
    ) = viewModelScope.launch {
        val result = deleteStoredPlaylist(database, GlobalYouTubeRepository.instance, playlist.id)
        if (result.isSuccess) {
            sharedViewModel.onItemDeleted(playlist)
            refresh(force = true)
        }
        onResult(result)
    }
}

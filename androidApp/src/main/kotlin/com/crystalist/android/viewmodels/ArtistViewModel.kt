package com.crystalist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crystalist.shared.api.artwork.AnimatedArtworkService
import com.crystalist.shared.db.MusicDatabase
import kotlinx.coroutines.CoroutineScope
import com.crystalist.shared.viewmodels.ArtistViewModel as SharedArtistViewModel

class ArtistViewModel(
    artistId: String? = null,
    animatedArtworkService: AnimatedArtworkService,
    database: MusicDatabase,
    applicationScope: CoroutineScope,
) : ViewModel() {
    val shared = SharedArtistViewModel(artistId, viewModelScope, animatedArtworkService, database, applicationScope)
}

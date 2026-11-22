package com.crystalist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.viewmodels.HomeViewModel as SharedHomeViewModel

class HomeViewModel(
    database: MusicDatabase,
    likedSongsName: String,
) : ViewModel() {
    val shared = SharedHomeViewModel(database, viewModelScope, likedSongsName = likedSongsName)
}

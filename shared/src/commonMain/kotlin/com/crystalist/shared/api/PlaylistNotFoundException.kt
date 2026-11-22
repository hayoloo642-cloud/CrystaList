package com.crystalist.shared.api

class PlaylistNotFoundException(
    val playlistId: String,
) : Exception()

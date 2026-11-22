package com.crystalist.shared.db

fun interface OfflineSongAvailability {
    suspend fun songIds(): Set<String>
}

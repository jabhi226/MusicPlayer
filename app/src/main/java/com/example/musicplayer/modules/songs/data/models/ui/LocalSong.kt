package com.example.musicplayer.modules.songs.data.models.ui

data class LocalSong(
    val id: String?,
    val data: String?,
    val displayName: String?,
    val artist: String?,
    val album: String?,
    val duration: String?,
    val imageUri: String? = null,
) {
}
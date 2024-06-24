package com.example.musicplayer.modules.songs.data.models.ui

import android.net.Uri
import com.example.musicplayer.modules.songs.data.models.network.SongDetails

data class LocalSong(
    val id: String?,
    val data: String?,
    val displayName: String?,
    val artist: String?,
    val album: String?,
    val duration: String?,
    val imageUri: String? = null,
    val songUri: Uri? = null,
) {
    fun getSongDetailsModel(): SongDetails {
        val s = SongDetails(
            id.hashCode(),
            "",
            null,
            null,
            null,
            null,
            null,
            displayName,
            artist,
            album,
            imageUri,
            false,
        )
        s.uri = songUri
        return s
    }
}
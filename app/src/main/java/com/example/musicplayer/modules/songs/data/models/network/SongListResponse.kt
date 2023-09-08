package com.example.musicplayer.modules.songs.data.models.network

import com.google.gson.annotations.SerializedName

data class SongListResponse(
    @SerializedName("data") val songDetails: List<SongDetails>
)
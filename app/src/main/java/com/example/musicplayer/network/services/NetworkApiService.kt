package com.example.musicplayer.network.services

import com.example.musicplayer.network.adapter.NetworkResponse
import com.example.musicplayer.modules.songs.data.models.network.SongListResponse
import retrofit2.http.GET

interface NetworkApiService {

    @GET("/items/songs")
    suspend fun getSongList(): NetworkResponse<SongListResponse, Error>
}
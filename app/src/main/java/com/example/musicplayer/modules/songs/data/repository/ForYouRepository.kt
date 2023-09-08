package com.example.musicplayer.modules.songs.data.repository

import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails

interface ForYouRepository {
    suspend fun getSongList(): Resource<List<SongDetails>>
}
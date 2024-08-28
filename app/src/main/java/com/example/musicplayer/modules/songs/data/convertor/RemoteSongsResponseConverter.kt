package com.example.musicplayer.modules.songs.data.convertor

import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.models.network.SongListResponse
import com.example.musicplayer.network.adapter.NetworkResponse

interface RemoteSongsResponseConverter {
    fun getSongList(api: NetworkResponse<SongListResponse, Error>): Resource<List<SongDetails>>
}
package com.example.musicplayer.modules.songs.data.repository

import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.network.adapter.NetworkResponse
import com.example.musicplayer.modules.songs.data.convertor.ResponseConvertor
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.network.services.NetworkApiService
import java.lang.Exception

class ForYouRepositoryImpl(
    private val networkApiService: NetworkApiService,
    private val responseConvertor: ResponseConvertor
) : ForYouRepository {
    override suspend fun getSongList(): Resource<List<SongDetails>> {
        return try {
            val api = networkApiService.getSongList()
            responseConvertor.getSongList(api)
        } catch (e: Exception){
            e.printStackTrace()
            Resource.error("")
        }
    }
}
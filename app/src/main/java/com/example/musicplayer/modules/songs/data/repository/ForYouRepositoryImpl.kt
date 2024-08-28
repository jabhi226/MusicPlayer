package com.example.musicplayer.modules.songs.data.repository

import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.convertor.RemoteSongsResponseConverter
import com.example.musicplayer.modules.songs.data.convertor.RemoteSongsResponseConverterImpl
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.network.services.NetworkApiService
import java.lang.Exception
import javax.inject.Inject

class ForYouRepositoryImpl @Inject constructor (
    private val networkApiService: NetworkApiService,
    private val responseConvertor: RemoteSongsResponseConverter
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
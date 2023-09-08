package com.example.musicplayer.modules.songs.data.convertor

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.musicplayer.R
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.models.network.SongListResponse
import com.example.musicplayer.network.adapter.NetworkResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResponseConvertor @Inject constructor(@ApplicationContext val context: Context) {
    fun getSongList(api: NetworkResponse<SongListResponse, Error>): Resource<List<SongDetails>> {
        return when (api) {
            is NetworkResponse.Success -> {
                Resource.success(api.body.songDetails)
            }

            else -> {
                Resource.error(context.getString(R.string.something_went_wrong))
            }
        }

    }
}
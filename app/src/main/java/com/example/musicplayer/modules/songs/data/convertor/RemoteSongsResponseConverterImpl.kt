package com.example.musicplayer.modules.songs.data.convertor

import android.content.Context
import com.example.musicplayer.R
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.models.network.SongListResponse
import com.example.musicplayer.network.adapter.NetworkResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RemoteSongsResponseConverterImpl @Inject constructor(
    @ApplicationContext val context: Context
) :
    RemoteSongsResponseConverter {
    override fun getSongList(api: NetworkResponse<SongListResponse, Error>): Resource<List<SongDetails>> {
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
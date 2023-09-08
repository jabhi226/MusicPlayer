package com.example.musicplayer.modules.songs.data.models.network

import android.graphics.Bitmap
import com.example.musicplayer.modules.songs.data.models.ui.Song
import com.google.gson.annotations.SerializedName

data class SongDetails(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("sort") var sort: Any? = null,
    @SerializedName("user_created") var userCreated: String? = null,
    @SerializedName("date_created") var dateCreated: String? = null,
    @SerializedName("user_updated") var userUpdated: String? = null,
    @SerializedName("date_updated") var dateUpdated: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("artist") var artist: String? = null,
    @SerializedName("accent") var accent: String? = null,
    @SerializedName("cover") var cover: String? = null,
    @SerializedName("top_track") var topTrack: Boolean? = null,
    @SerializedName("url") var url: String? = null
) {
}
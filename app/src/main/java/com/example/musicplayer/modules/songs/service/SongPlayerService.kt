package com.example.musicplayer.modules.songs.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception


class SongPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = SongPlayerBinder()
    private var currentSongDataPoint: String? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    fun startSong(song: SongDetails) {
        if (currentSongDataPoint == song.url) return
        currentSongDataPoint = song.url
        try {
            if (isSongPlaying() == true){
                mediaPlayer?.reset()
            }
            try {
                mediaPlayer?.setDataSource(song.url)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            mediaPlayer?.setOnPreparedListener {
                it.start()
            }
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    fun pauseSong() {
        try {
            mediaPlayer?.pause()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun getDurations(): Int? {
        return mediaPlayer?.duration
    }

    fun getCurrentPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun resumeSong() {
        mediaPlayer?.start()
    }

    fun seekTo(seekTimeInMilliseconds: Int) {
        println("poiuy $seekTimeInMilliseconds")
        mediaPlayer?.seekTo(seekTimeInMilliseconds)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun isSongPlaying(): Boolean? {
        return mediaPlayer?.isPlaying
    }

    inner class SongPlayerBinder : Binder() {
        fun getSongPlayerService(): SongPlayerService {
            return this@SongPlayerService
        }
    }
}
package com.example.musicplayer.modules.songs.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.NEXT
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.PLAY
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.PREVIOUS
import com.example.musicplayer.modules.songs.ui.fragments.ForYouFragment


class SongPlayerNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        when(intent?.action){
            //only play next or prev song, when music list contains more than one song
            PREVIOUS -> {
                ForYouFragment.playPrevious()
            }
            PLAY -> {
                ForYouFragment.startStopPlayer()
            }
            NEXT -> {
                ForYouFragment.playNext()
            }
        }
    }

}
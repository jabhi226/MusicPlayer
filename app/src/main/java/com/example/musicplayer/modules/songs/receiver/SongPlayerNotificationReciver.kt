package com.example.musicplayer.modules.songs.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicplayer.modules.songs.helper.SongType
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.NEXT
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.PLAY
import com.example.musicplayer.modules.songs.service.SongPlayerService.Companion.PREVIOUS
import com.example.musicplayer.modules.songs.ui.fragments.SongFragment


class SongPlayerNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        val type: SongType = if (intent?.extras?.getBoolean("isRemote") == true) {SongType.REMOTE_SONG} else {SongType.LOCAL_SONG}
        when(intent?.action){
            //only play next or prev song, when music list contains more than one song
            PREVIOUS -> {

                SongFragment.playPrevious(type)
            }
            PLAY -> {
                SongFragment.startStopPlayer()
            }
            NEXT -> {
                SongFragment.playNext(type)
            }
        }
    }

}
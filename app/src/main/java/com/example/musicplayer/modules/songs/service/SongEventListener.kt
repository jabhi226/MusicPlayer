package com.example.musicplayer.modules.songs.service

import com.example.musicplayer.modules.songs.helper.SongType

interface SongEventListener {
    fun playPreviousSong(songType: SongType)
    fun playNextSong(songType: SongType)
}
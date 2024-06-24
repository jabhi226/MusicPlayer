package com.example.musicplayer.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        const val MUSIC_PLAYER_NOTIFICATION_CHANNEL = "MUSIC_PLAYER_NOTIFICATION_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            MUSIC_PLAYER_NOTIFICATION_CHANNEL,
            "Music Player",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Show media player notification"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
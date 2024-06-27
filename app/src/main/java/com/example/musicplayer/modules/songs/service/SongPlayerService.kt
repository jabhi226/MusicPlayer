package com.example.musicplayer.modules.songs.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.example.musicplayer.R
import com.example.musicplayer.application.MyApp
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.helper.SongType
import com.example.musicplayer.modules.songs.receiver.SongPlayerNotificationReceiver


class SongPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = SongPlayerBinder()
    private var currentSongDataPoint: String? = null
    private var songEventListener: SongEventListener? = null

    companion object {
        const val PLAY = "PLAY"
        const val NEXT = "NEXT"
        const val PREVIOUS = "PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    fun startSong(song: SongDetails) {
        if (currentSongDataPoint == (song.url ?: song.uri.toString())) return
        currentSongDataPoint = song.url ?: song.uri.toString()
        try {
            mediaPlayer?.reset()
            if (song.url != null) {
                mediaPlayer?.setDataSource(song.url)
            } else {
                song.uri?.let { mediaPlayer?.setDataSource(this, it) }
            }
            mediaPlayer?.setOnPreparedListener {
                it.start()
            }
            mediaPlayer?.prepareAsync()
            showNotification(song, song.url != null)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    fun setEventListeners(songEventListener: SongEventListener){
        this.songEventListener = songEventListener
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
        mediaPlayer?.seekTo(seekTimeInMilliseconds)
    }

    private fun showNotification(
        song: SongDetails,
        isRemote: Boolean
    ) {
        val mediaSession = MediaSessionCompat(this, "music_player")

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val prevIntent =
            Intent(baseContext, SongPlayerNotificationReceiver::class.java).setAction(PREVIOUS)
        prevIntent.putExtra("isRemote", isRemote)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, SongPlayerNotificationReceiver::class.java).setAction(PLAY)
        playIntent.putExtra("isRemote", isRemote)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent =
            Intent(baseContext, SongPlayerNotificationReceiver::class.java).setAction(NEXT)
        nextIntent.putExtra("isRemote", isRemote)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val notification = androidx.core.app.NotificationCompat.Builder(
            this,
            MyApp.MUSIC_PLAYER_NOTIFICATION_CHANNEL
        )
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            // Add media control buttons that invoke intents in your media service
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
            .addAction(android.R.drawable.ic_media_pause, "Pause", playPendingIntent) // #1
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent) // #2
//            .setContentIntent(contentIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setContentTitle(song.name)
            .setContentText(song.artist)
            .setOnlyAlertOnce(true) // show notification only once
            .setOngoing(true) // set notification ongoing to make it non cancelable
            .setAutoCancel(false)
            .build()

        startForeground(1001, notification)

    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun isSongPlaying(): Boolean? {
        return mediaPlayer?.isPlaying
    }

    fun nextSong(songType: SongType) {
        songEventListener?.playNextSong(songType)
    }

    fun previousSong(songType: SongType) {
        songEventListener?.playPreviousSong(songType)
    }

    inner class SongPlayerBinder : Binder() {
        fun getSongPlayerService(): SongPlayerService {
            return this@SongPlayerService
        }
    }
}
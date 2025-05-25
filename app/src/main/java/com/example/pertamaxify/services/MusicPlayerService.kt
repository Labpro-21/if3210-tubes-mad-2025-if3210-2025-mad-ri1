package com.example.pertamaxify.services

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.pertamaxify.CHANNEL_ID
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PREV = "prev"
const val NEXT = "next"
const val PLAY_PAUSE = "play_pause"

class MusicPlayerService: Service() {

    val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService

        fun setSong(song: Song) {
            this@MusicPlayerService.currentSong = MutableStateFlow(song)
        }

        fun currentDuration() = this@MusicPlayerService.currentDuration

        fun maxDuration() = this@MusicPlayerService.maxDuration

        fun isPlaying() = this@MusicPlayerService.isPlaying

        fun getCurrentTrack() = this@MusicPlayerService.currentSong

    }

    private val isPlaying = MutableStateFlow(false)

    var mediaPlayer = MediaPlayer()

    private val maxDuration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)

    private val scope = CoroutineScope(Dispatchers.Main)

    private var job: Job? = null

    private var currentSong = MutableStateFlow<Song>(
        Song(
            id = 0,
            title = "No song selected",
            artist = "",
            artwork = "",
            url = "",
        )
    )

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                PREV -> {
                    prev()
                }
                NEXT -> {
                    next()
                }
                PLAY_PAUSE -> {
                    playPause()
                }
                else -> {
                    currentSong.update { it }
                }
            }
        }

        return START_STICKY
    }

    fun prev() {
        // Handle previous song action
    }

    fun next() {
        // Handle next song action
    }

    fun playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        sendNotification(currentSong.value)
    }

    fun updateDurations() {
        job = scope.launch {
            try {
                if (mediaPlayer.isPlaying.not()) return@launch
                maxDuration.update { mediaPlayer.duration.toFloat() }
                while (true) {
                    currentDuration.update { mediaPlayer.currentPosition.toFloat() }
                    delay(1000)
                }
            }catch (e:IllegalStateException){
                e.printStackTrace()
            }


        }
    }

    private fun play(song: Song) {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, song.url.toUri())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            sendNotification(song)
            updateDurations()
        }
    }

    private fun sendNotification(song: Song) {

        val session = MediaSessionCompat(this,"music")

        isPlaying.update { mediaPlayer.isPlaying }
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(session.sessionToken)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .addAction(R.drawable.skip_prev, "prev", createPrevPendingIntent())
            .addAction(
                if (mediaPlayer.isPlaying) R.drawable.pause else R.drawable.play_arrow,
                "play_pause",
                createPlayPausePendingIntent()
            )
            .addAction(R.drawable.skip_next, "next", createNextPendingIntent())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startForeground(1, notification)
            }
        } else {
            startForeground(1, notification)
        }
    }

    fun createPrevPendingIntent(): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            action = PREV
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            action = PLAY_PAUSE
        }
        return PendingIntent.getService(
            this, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun createNextPendingIntent(): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            action = NEXT
        }
        return PendingIntent.getService(
            this, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer.stop()
        mediaPlayer.release()
        return super.onUnbind(intent)
    }

}
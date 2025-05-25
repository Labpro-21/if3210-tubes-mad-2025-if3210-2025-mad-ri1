package com.example.pertamaxify.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pertamaxify.CHANNEL_ID
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var currentSong: Song? = null
    private var isFromServer: Boolean = false
    private var serverId: Int = -1

    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize media session
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(mediaSessionCallback)
            isActive = true
        }

        // Initialize notification
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )

        // Setup ExoPlayer listener
        exoPlayer.addListener(playerListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> playPause()
            ACTION_NEXT -> next()
            ACTION_PREVIOUS -> previous()
            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaSession.release()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    fun playSong(song: Song, fromServer: Boolean = false, serverId: Int = -1) {
        currentSong = song
        isFromServer = fromServer
        this.serverId = serverId

        val mediaItem = MediaItem.fromUri(song.url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        updateNotification()
        updateMediaSession()

        // Start foreground service
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun next() {
        // Implement next logic based on your playlist
    }

    fun previous() {
        // Implement previous logic based on your playlist
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun getPlayer(): ExoPlayer = exoPlayer

    private fun updateNotification() {
        if (currentSong != null) {
            val notification = buildNotification()
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(): Notification {
        val song = currentSong ?: return notificationBuilder.build()

        // Load album art
        loadAlbumArt(song.artwork)

        // Play/Pause action
        val playPauseAction = NotificationCompat.Action(
            if (exoPlayer.isPlaying) R.drawable.pause else R.drawable.ic_play,
            if (exoPlayer.isPlaying) "Pause" else "Play",
            createPendingIntent(ACTION_PLAY_PAUSE)
        )

        // Previous action
        val previousAction = NotificationCompat.Action(
            R.drawable.skip_prev,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        )

        // Next action
        val nextAction = NotificationCompat.Action(
            R.drawable.skip_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        )

        return notificationBuilder
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(if (isFromServer) "Streaming" else "Local")
            .clearActions()
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .build()
    }

    private fun loadAlbumArt(artworkUrl: String?) {
        if (artworkUrl.isNullOrBlank()) {
            notificationBuilder.setLargeIcon(
                BitmapFactory.decodeResource(resources, R.drawable.song_image_placeholder)
            )
            return
        }

        Glide.with(this)
            .asBitmap()
            .load(artworkUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notificationBuilder.setLargeIcon(resource)
                    return
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    notificationBuilder.setLargeIcon(
                        BitmapFactory.decodeResource(resources, R.drawable.song_image_placeholder)
                    )
                }
            })
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateMediaSession() {
        val song = currentSong ?: return

        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.artwork)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration?.toLong() ?: 0L)
            .build()

        mediaSession.setMetadata(metadata)
        updatePlaybackState()
    }

    private fun updatePlaybackState() {
        val state = if (exoPlayer.isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, exoPlayer.currentPosition, 1f)
            .build()

        mediaSession.setPlaybackState(playbackState)
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updateNotification()
            updatePlaybackState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateNotification()
            updatePlaybackState()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updatePlaybackState()
        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            exoPlayer.play()
        }

        override fun onPause() {
            exoPlayer.pause()
        }

        override fun onSkipToNext() {
            next()
        }

        override fun onSkipToPrevious() {
            previous()
        }

        override fun onSeekTo(pos: Long) {
            exoPlayer.seekTo(pos)
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.pertamaxify.PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.pertamaxify.NEXT"
        const val ACTION_PREVIOUS = "com.example.pertamaxify.PREVIOUS"
        const val ACTION_STOP = "com.example.pertamaxify.STOP"
        const val NOTIFICATION_ID = 1
    }
}
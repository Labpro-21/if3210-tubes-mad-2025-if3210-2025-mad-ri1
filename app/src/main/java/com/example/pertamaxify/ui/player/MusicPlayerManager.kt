package com.example.pertamaxify.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.media3.exoplayer.ExoPlayer
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.services.MusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var musicService: MusicService? = null
    private var isBound = false

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private var isFromServer = false
    private var serverId = -1

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            _isServiceConnected.value = true

            musicService?.getPlayer()?.addListener(playerListener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
            _isServiceConnected.value = false
        }
    }

    private val playerListener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPositionDiscontinuity(
            oldPosition: androidx.media3.common.Player.PositionInfo,
            newPosition: androidx.media3.common.Player.PositionInfo,
            reason: Int
        ) {
            _currentPosition.value = newPosition.positionMs
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun playSong(song: Song, fromServer: Boolean = false, serverId: Int = -1) {
        _currentSong.value = song
        isFromServer = fromServer
        this.serverId = serverId

        if (musicService == null) {
            val intent = Intent(context, MusicService::class.java)
            context.startService(intent)
                        CoroutineScope(Dispatchers.Main).launch {
                isServiceConnected.collect { connected ->
                    if (connected && musicService != null) {
                        musicService?.playSong(song, fromServer, serverId)
                        cancel()                     }
                }
            }
        } else {
            musicService?.playSong(song, fromServer, serverId)
        }
    }

    fun playPause() {
        musicService?.playPause()
    }

    fun seekTo(position: Long) {
        musicService?.seekTo(position)
    }

    fun next() {
        musicService?.next()
    }

    fun previous() {
        musicService?.previous()
    }

    fun getPlayer(): ExoPlayer? = musicService?.getPlayer()

    fun updatePosition(position: Long) {
        _currentPosition.value = position
    }

    fun updateDuration(duration: Long) {
        _duration.value = duration
    }

    fun release() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }
}
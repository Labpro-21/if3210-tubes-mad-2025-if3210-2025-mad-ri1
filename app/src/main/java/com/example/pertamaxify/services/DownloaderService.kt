package com.example.pertamaxify.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.data.repository.SongRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DownloaderService : Service() {

    @Inject
    lateinit var songRepository: SongRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var notificationManager: NotificationManager
    private val notificationId = 1
    private val channelId = "download_channel"

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        Log.d("DownloaderService", "Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            Log.d("DownloaderService", "Service Started with action: ${it.action}")
            when (it.action) {
                ACTION_DOWNLOAD_SONG -> {
                    val songId = it.getIntExtra(EXTRA_SONG_ID, -1)
                    val title = it.getStringExtra(EXTRA_SONG_TITLE) ?: "Unknown"
                    val artist = it.getStringExtra(EXTRA_SONG_ARTIST) ?: "Unknown"
                    val artwork = it.getStringExtra(EXTRA_SONG_ARTWORK)
                    val url = it.getStringExtra(EXTRA_SONG_URL)
                    val duration = it.getIntExtra(EXTRA_SONG_DURATION, 0)
                    val email = it.getStringExtra(EXTRA_EMAIL)

                    if (songId != -1 && url != null) {
                        downloadSong(songId, title, artist, artwork, url, duration, email)
                    }
                }
            }
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Downloading Music")
            .setContentText("Download in progress")
            .setSmallIcon(R.drawable.ic_download)
            .setProgress(0, 0, true)
            .build()

        startForeground(notificationId, notification)

        return START_NOT_STICKY
    }

    fun downloadSong(
        songId: Int,
        title: String,
        artist: String,
        artwork: String?,
        url: String,
        duration: Int,
        email: String? = null
    ) {
        serviceScope.launch {
            try {
                Log.d("DownloaderService", "Downloading song: $title from $url")
                updateNotification("Downloading $title", "Starting download...")

                // Create directory if doesn't exist
                val musicDir = File(applicationContext.filesDir, "music")
                if (!musicDir.exists()) {
                    musicDir.mkdirs()
                }

                // Generate a unique filename
                val fileName = "${UUID.randomUUID()}.mp3"
                val destFile = File(musicDir, fileName)

                URL(url).openStream().use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val artworkDir = File(applicationContext.filesDir, "artwork")
                if (!artworkDir.exists()) {
                    artworkDir.mkdirs()
                }

                var localArtworkPath: String? = null
                if (!artwork.isNullOrEmpty()) {
                    val artworkFileName = "${UUID.randomUUID()}.jpg"
                    val artworkFile = File(artworkDir, artworkFileName)

                    try {
                        URL(artwork).openStream().use { input ->
                            FileOutputStream(artworkFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        localArtworkPath = artworkFile.absolutePath
                    } catch (e: Exception) {
                        Log.e("DownloaderService", "Error downloading artwork", e)
                    }
                }

                // Save to database
                val song = Song(
                    title = title,
                    artist = artist,
                    artwork = localArtworkPath,
                    url = destFile.absolutePath,
                    duration = duration,
                    isDownloaded = true,
                    addedTime = Date(),
                    addedBy = email
                )

                songRepository.upsertSong(song)

                // Update notification
                updateNotification("Download Complete", "$title has been downloaded")

                serviceScope.launch {
                    kotlinx.coroutines.delay(3000)
                    stopSelf()
                }

                Log.d("DownloaderService", "Download completed: $title")

            } catch (e: Exception) {
                Log.e("DownloaderService", "Error downloading song", e)
                updateNotification("Download Failed", "Could not download $title")

                serviceScope.launch {
                    kotlinx.coroutines.delay(3000)
                    stopSelf()
                }
            }
        }
    }

    private fun updateNotification(title: String, content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_download)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Channel"
            val descriptionText = "Channel for music download notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_DOWNLOAD_SONG = "com.example.pertamaxify.action.DOWNLOAD_SONG"
        const val EXTRA_SONG_ID = "com.example.pertamaxify.extra.SONG_ID"
        const val EXTRA_SONG_TITLE = "com.example.pertamaxify.extra.SONG_TITLE"
        const val EXTRA_SONG_ARTIST = "com.example.pertamaxify.extra.SONG_ARTIST"
        const val EXTRA_SONG_ARTWORK = "com.example.pertamaxify.extra.SONG_ARTWORK"
        const val EXTRA_SONG_URL = "com.example.pertamaxify.extra.SONG_URL"
        const val EXTRA_SONG_DURATION = "com.example.pertamaxify.extra.SONG_DURATION"
        const val EXTRA_EMAIL = "com.example.pertamaxify.extra.EMAIL"

        fun startDownload(
            context: Context,
            songResponse: SongResponse,
            email: String? = null
        ) {
            val intent = Intent(context, DownloaderService::class.java).apply {
                action = ACTION_DOWNLOAD_SONG
                putExtra(EXTRA_SONG_ID, songResponse.serverId)
                putExtra(EXTRA_SONG_TITLE, songResponse.title)
                putExtra(EXTRA_SONG_ARTIST, songResponse.artist)
                putExtra(EXTRA_SONG_ARTWORK, songResponse.artwork)
                putExtra(EXTRA_SONG_URL, songResponse.url)
                putExtra(EXTRA_SONG_DURATION, songResponse.convertDurationToSeconds(songResponse.duration))
                putExtra(EXTRA_EMAIL, email)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun SongResponse.convertDurationToSeconds(duration: String): Int {
            try {
                val parts = duration.split(":")
                if (parts.size == 2) {
                    val minutes = parts[0].toInt()
                    val seconds = parts[1].toInt()
                    return minutes * 60 + seconds
                }
            } catch (e: Exception) {
                Log.e("SongResponse", "Error converting duration to seconds: ${e.message}")
            }
            return 0
        }
    }
}

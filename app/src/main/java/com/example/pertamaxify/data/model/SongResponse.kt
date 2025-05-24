package com.example.pertamaxify.data.model

import android.util.Log
import java.util.Date

data class SongResponse(
    val serverId: Int,
    val title: String,
    val artist: String,
    val artwork: String,
    val url: String,
    val duration: String, // mm:ss
    val country: String,
    val rank: Int,
    val createdAt: String,
    val updatedAt: String
) {
    fun toSong(): Song {
        return Song(
            title = title,
            artist = artist,
            artwork = artwork,
            url = url,
            duration = convertDurationToSeconds(duration),
            isDownloaded = false,
            isLiked = false,
            addedTime = Date()
        )
    }

    // Convert duration string (mm:ss) to seconds
    private fun convertDurationToSeconds(duration: String): Int {
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

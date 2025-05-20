package com.example.pertamaxify.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SongResponse(
    val id: Int,
    val title: String,
    val artist: String,
    val artwork: String,
    val url: String,
    val duration: String, // in format "mm:ss"
    val country: String,
    val rank: Int,
    val createdAt: String,
    val updatedAt: String
) {
    // Convert to Song entity for local database
    fun toSong(): Song {
        return Song(
            id = 0, // Auto-generated
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
            // Return 0 if parsing fails
        }
        return 0
    }
}

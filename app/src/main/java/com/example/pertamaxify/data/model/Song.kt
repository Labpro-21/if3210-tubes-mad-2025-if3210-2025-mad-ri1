package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pertamaxify.utils.Converters
import java.util.Date

@Entity(tableName = "song")
@TypeConverters(Converters::class)
data class Song(
    val title: String,
    val artist: String,
    val artwork: String? = null, // Image path
    val url: String, // Audio path

    val addedBy: String? = null, // email of user who added this song, with assumption the email is unique
    val isLiked: Boolean? = false, // is the song liked by user who added it
    val addedTime: Date = Date(), // When the song was added to the database
    val recentlyPlayed: Date? = null, // When the song was last played by the user who added it

    val isDownloaded: Boolean? = false, // Is the song downloaded from the server

    val duration: Int? = null, // Duration in seconds

    val numberOfPlay: Int? = 0, // Number of times the song has been played

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
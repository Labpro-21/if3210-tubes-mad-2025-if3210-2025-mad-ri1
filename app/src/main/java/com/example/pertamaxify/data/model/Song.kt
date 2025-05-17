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
    val singer: String,
    val imagePath: String, // Image path
    val audioPath: String, // Audio path
    // email of user who added this song, with assumption the email is unique
    val addedBy: String? = null,
    val isLiked: Boolean? = false, // is the song liked by user who added it
    val addedTime: Date = Date(), // When the song was added to the database
    val recentlyPlayed: Date? = null, // When the song was last played by the user who added it

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
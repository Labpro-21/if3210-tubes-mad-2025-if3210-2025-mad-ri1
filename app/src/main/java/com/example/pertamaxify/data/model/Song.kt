package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class Song(
    val title: String,
    val singer: String,
    val imagePath: String, // Image path
    val audioPath: String, // Audio path
    val addedBy: String? = null, // Email of user who added this song
    val lastPlayed: String? = null, // Date when this song last played by the user added it
    val isLiked: Boolean? = false, // is the song liked by user who added it

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
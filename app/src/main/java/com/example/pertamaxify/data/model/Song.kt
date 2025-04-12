package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class Song(
    val title: String,
    val singer: String,
    val imagePath: String, // Image path
    val audioPath: String, // Audio path
    // Username of user who added this song, with assumption the username is unique
    val addedBy: String? = null,
    val isLiked: Boolean? = false, // is the song liked by user who added it

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
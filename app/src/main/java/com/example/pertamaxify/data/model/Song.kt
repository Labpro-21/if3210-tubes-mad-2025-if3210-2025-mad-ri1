package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    val title: String,
    val singer: String,
    val image: String, // Image path
    val audio: String, // Audio path
    val addedAt: String? = "", // When the song added to app
    val recentlyPlayed: String? = "", // When you last played this song huh
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
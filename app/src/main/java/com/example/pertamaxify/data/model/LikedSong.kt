package com.example.pertamaxify.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["userEmail", "songId"])
data class LikedSong(
    val userEmail: String,
    val songId: Int
)
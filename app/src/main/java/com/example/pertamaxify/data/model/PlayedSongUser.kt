package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayedSongUser(
    @PrimaryKey
    val songId: Int,
    val userId: Int
)

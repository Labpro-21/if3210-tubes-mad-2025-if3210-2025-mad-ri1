package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayedSongUser(
    @PrimaryKey
    val song: Song,
    val user: User
)

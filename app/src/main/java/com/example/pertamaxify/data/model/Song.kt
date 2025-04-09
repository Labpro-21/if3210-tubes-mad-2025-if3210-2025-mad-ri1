package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    val title: String,
    val singer: String,
    val image: String, // Image path
    val audio: String, // Audio path

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
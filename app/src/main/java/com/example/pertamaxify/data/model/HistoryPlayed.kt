package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryPlayed(
    val songId: Int,
    val email: String? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

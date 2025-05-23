package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pertamaxify.utils.Converters

@Entity(tableName = "statistic")
@TypeConverters(Converters::class)
data class Statistic (
    val songId: Int,
    val playedAt: Long,
    val playedBy: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
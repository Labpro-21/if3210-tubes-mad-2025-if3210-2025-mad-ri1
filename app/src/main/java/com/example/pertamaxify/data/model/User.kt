package com.example.pertamaxify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    val username: String,
    val imageProfile: String?,
    val country: String?,
    @PrimaryKey
    val email: String,
)

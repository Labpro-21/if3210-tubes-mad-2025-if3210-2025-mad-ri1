package com.example.pertamaxify.data.model

data class ProfileResponse(
    val id: String,
    val username: String,
    val email: String,
    val profilePhoto: String,
    val location: String,
    val createdAt: String,
    val updatedAt: String
)
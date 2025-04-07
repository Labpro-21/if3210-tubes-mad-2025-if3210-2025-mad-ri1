package com.example.pertamaxify.data.model

data class JwtPayload(
    val id: Int,
    val username: String,
    val iat: Long,
    val exp: Long
)

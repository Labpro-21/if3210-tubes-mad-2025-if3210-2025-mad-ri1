package com.example.pertamaxify.data.remote

import com.example.pertamaxify.data.model.LoginRequest
import com.example.pertamaxify.data.model.LoginResponse
import com.example.pertamaxify.data.model.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>
}

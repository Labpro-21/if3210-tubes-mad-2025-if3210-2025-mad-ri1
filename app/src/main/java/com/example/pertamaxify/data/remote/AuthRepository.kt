package com.example.pertamaxify.data.remote

import com.example.pertamaxify.data.model.LoginRequest
import com.example.pertamaxify.data.model.LoginResponse
import com.example.pertamaxify.data.model.ProfileResponse
import com.example.pertamaxify.data.model.RefreshTokenRequest
import retrofit2.Response

class AuthRepository {
    private val apiService = ApiClient.instance

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun refreshToken(refreshToken: String): Response<LoginResponse> {
        return apiService.refreshToken(RefreshTokenRequest(refreshToken))
    }

    suspend fun verifyToken(accessToken: String): Boolean {
        val response = apiService.verifyToken("Bearer $accessToken")
        return response.code() != 403
    }

    suspend fun getProfile(accessToken: String): Response<ProfileResponse> {
        return apiService.getProfile("Bearer $accessToken")
    }
}

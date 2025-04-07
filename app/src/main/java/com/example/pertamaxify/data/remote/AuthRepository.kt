package com.example.pertamaxify.data.remote

import com.example.pertamaxify.data.model.LoginRequest
import com.example.pertamaxify.data.model.LoginResponse
import retrofit2.Response

class AuthRepository {
    private val apiService = ApiClient.instance

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }
}

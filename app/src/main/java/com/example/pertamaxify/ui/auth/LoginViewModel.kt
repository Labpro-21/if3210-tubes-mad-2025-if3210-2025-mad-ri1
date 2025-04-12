package com.example.pertamaxify.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.model.ErrorResponse
import com.example.pertamaxify.data.remote.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)

    fun login(
        email: String,
        password: String,
        onSuccess: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val accessToken = loginResponse.accessToken
                        val refreshToken = loginResponse.refreshToken
                        onSuccess(accessToken, refreshToken)
                    } ?: run {
                        errorMessage = "Login failed: Response body is null"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
                            ?: "Unknown error"
                    } catch (e: Exception) {
                        "Failed to parse error response"
                    }
                    this@LoginViewModel.errorMessage = errorMessage
                }
            } catch (e: Exception) {
                errorMessage = "An error occurred: ${e.message}"
            }
        }
    }
}

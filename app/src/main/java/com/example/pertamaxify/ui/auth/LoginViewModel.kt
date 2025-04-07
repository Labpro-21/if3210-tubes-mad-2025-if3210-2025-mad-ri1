package com.example.pertamaxify.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.model.ErrorResponse
import com.example.pertamaxify.data.remote.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val token = loginResponse.accessToken
                        Log.d("LoginViewModel", "Login successful: Access Token: $token")
                        onSuccess(token)
                    } ?: run {
                        Log.e("LoginViewModel", "Login failed: Response body is null")
                        onError("Login failed: Response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
                            ?: "Unknown error"
                    } catch (e: Exception) {
                        "Failed to parse error response"
                    }
                    Log.e("LoginViewModel", "Login failed: $errorMessage")
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred: ${e.message}"
                Log.e("LoginViewModel", errorMessage, e)
                onError(errorMessage)
            }
        }
    }
}

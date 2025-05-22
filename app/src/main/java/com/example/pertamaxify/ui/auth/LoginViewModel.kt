package com.example.pertamaxify.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.model.ErrorResponse
import com.example.pertamaxify.data.model.User
import com.example.pertamaxify.data.remote.AuthRepository
import com.example.pertamaxify.data.repository.UserRepository
import com.example.pertamaxify.ui.network.NetworkUtils
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: (String, String) -> Unit
    ) {
        isLoading = true

        viewModelScope.launch {
            try {
                if (!NetworkUtils.isNetworkConnected(context)) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "No internet connection. Logging in offline."
                        isLoading = false
                        onSuccess("", "")
                    }
                    return@launch
                }

                // API call to login - can be done on IO dispatcher
                val response = withContext(Dispatchers.IO) {
                    authRepository.login(email, password)
                }

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val accessToken = loginResponse.accessToken
                        val refreshToken = loginResponse.refreshToken

                        // Upsert user into the database - must be on IO dispatcher
                        withContext(Dispatchers.IO) {
                            userRepository.upsertUser(User(
                                email = email,
                                username = email.substringBefore("@"),
                                imageProfile = null,
                                country = "Indonesia",
                            ))
                        }

                        // Return to Main thread to update UI
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            onSuccess(accessToken, refreshToken)
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            errorMessage = "Login failed: Response body is null"
                            isLoading = false
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
                            ?: "Unknown error"
                    } catch (e: Exception) {
                        "Failed to parse error response"
                    }

                    withContext(Dispatchers.Main) {
                        errorMessage = errorMsg
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "An error occurred: ${e.message}"
                    isLoading = false
                }
            }
        }
    }
}

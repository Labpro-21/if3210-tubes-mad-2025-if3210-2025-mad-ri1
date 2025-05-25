package com.example.pertamaxify.data.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.remote.ApiClient
import com.example.pertamaxify.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // UI State
    private val _uiState = mutableStateOf(ProfileUiState())
    val uiState = _uiState

    // Loading state (now part of UI state)
    // Error messages (now part of UI state)

    fun updateProfile(
        token: String,
        countryCode: String?,
        profilePhoto: File?
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {
            when (val result = profileRepository.updateProfile(
                token = token,
                countryCode = countryCode,
                profilePhoto = profilePhoto
            )) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        successMessage = "Profile updated successfully",
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        successMessage = null,
                        errorMessage = result.message ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    fun refetchProfile(client: ApiClient) {
        try {
            val res = client.getProfile("Bearer $token")
            if (res.isSuccessful) {
                profile = res.body()
                showNoConnection = false
            } else {
                Log.e("ProfileScreen", "API Error: ${res.code()}")
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Exception: ${e.localizedMessage}")
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
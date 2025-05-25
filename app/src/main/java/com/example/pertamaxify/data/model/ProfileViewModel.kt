package com.example.pertamaxify.data.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.remote.ApiClient
import com.example.pertamaxify.data.repository.ProfileRepository
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.data.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileData(
    val email: String,
    val songCount: Int,
    val listenedCount: Int,
    val numberOfLikedSong: Int
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val songRepository: SongRepository,
    private val statisticRepository: StatisticRepository
) : ViewModel() {


    private val _uiState = mutableStateOf(ProfileUiState())
    val uiState = _uiState

    private val _profileData = mutableStateOf<ProfileData?>(null)


    fun refreshData(email: String) {
        updateProfileStatistic(email)
    }

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

    fun updateProfileStatistic(
        email: String
    ) {
        viewModelScope.launch {
            try {
                val songCount = songRepository.getAllSongsByEmail(email).count()
                val totalLikedSong = songRepository.getAllLikedSongsByEmail(email).count()
                val listenedCount = statisticRepository.getAllStatisticByEmail(email).count()

                _profileData.value = ProfileData(
                    email = email,
                    songCount = songCount,
                    listenedCount = listenedCount,
                    numberOfLikedSong = totalLikedSong
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile statistics", e)
            }
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
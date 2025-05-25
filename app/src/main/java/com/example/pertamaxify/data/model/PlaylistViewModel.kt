package com.example.pertamaxify.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    // Global top songs
    private val _globalTopSongs = MutableStateFlow<List<SongResponse>>(emptyList())
    val globalTopSongs: StateFlow<List<SongResponse>> = _globalTopSongs

    // Country top songs
    private val _countryTopSongs = MutableStateFlow<List<SongResponse>>(emptyList())
    val countryTopSongs: StateFlow<List<SongResponse>> = _countryTopSongs

    // Currently selected country
    val selectedCountry = mutableStateOf("ID") // Default to Indonesia

    // Loading state
    val isLoadingGlobal = mutableStateOf(false)
    val isLoadingCountry = mutableStateOf(false)

    // Error messages
    val globalErrorMessage = mutableStateOf<String?>(null)
    val countryErrorMessage = mutableStateOf<String?>(null)

    init {
        fetchGlobalTopSongs()
        fetchCountryTopSongs(selectedCountry.value)
    }

    fun fetchGlobalTopSongs() {
        isLoadingGlobal.value = true
        globalErrorMessage.value = null

        viewModelScope.launch {
            try {
                val songs = playlistRepository.getGlobalTopSongs()
                _globalTopSongs.value = songs

                if (songs.isEmpty()) {
                    globalErrorMessage.value = "No songs found in the global playlist."
                }
            } catch (e: Exception) {
                globalErrorMessage.value = "Error loading global playlist: ${e.message}"
            } finally {
                isLoadingGlobal.value = false
            }
        }
    }

    fun fetchCountryTopSongs(countryCode: String) {
        // Update selected country
        selectedCountry.value = countryCode

        isLoadingCountry.value = true
        countryErrorMessage.value = null

        viewModelScope.launch {
            try {
                if (countryCode !in playlistRepository.supportedCountries) {
                    _countryTopSongs.value = emptyList()
                    countryErrorMessage.value = "The server has no songs for this country."
                    return@launch
                }

                val songs = playlistRepository.getCountryTopSongs(countryCode)
                _countryTopSongs.value = songs

                if (songs.isEmpty()) {
                    countryErrorMessage.value =
                        "No songs found for ${playlistRepository.countryNames[countryCode] ?: countryCode}."
                }
            } catch (e: Exception) {
                countryErrorMessage.value = "Error loading country playlist: ${e.message}"
            } finally {
                isLoadingCountry.value = false
            }
        }
    }

    /**
     * Get country name from country code
     */
    fun getCountryName(countryCode: String): String {
        return playlistRepository.countryNames[countryCode] ?: countryCode
    }

    /**
     * Get list of supported countries
     */
    fun getSupportedCountries(): List<String> {
        return playlistRepository.supportedCountries
    }

    suspend fun getSongByServerId(songId: Int): SongResponse? {
        return playlistRepository.getSongById(songId)
    }
}
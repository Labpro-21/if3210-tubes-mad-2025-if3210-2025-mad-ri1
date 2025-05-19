package com.example.pertamaxify.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _recentlyPlayedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyPlayedSongs: StateFlow<List<Song>> = _recentlyPlayedSongs

    private val _recentlyAddedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyAddedSongs: StateFlow<List<Song>> = _recentlyAddedSongs

    // Flag to track when data should be refreshed
    private val _dataRefreshNeeded = mutableStateOf(true)

    init {
        refreshAllData()
    }

    // Call this function when you want to refresh all data
    fun refreshAllData() {
        fetchRecentlyPlayedSongs()
        fetchRecentlyAddedSongs()
        _dataRefreshNeeded.value = false
    }

    fun fetchRecentlyPlayedSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getRecentlyPlayedSongs()
            _recentlyPlayedSongs.value = songs
        }
    }

    fun fetchRecentlyAddedSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getRecentlyAddedSongs()
            _recentlyAddedSongs.value = songs
        }
    }

    // Call this when a song is played to update its recentlyPlayed timestamp
    fun updateSongPlayedTimestamp(song: Song, email: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (email.isNullOrEmpty()) return@launch

            try {
                // Only update if we have a valid email
                val updatedSong = song.copy(
                    recentlyPlayed = Date()
                )

                songRepository.updateSong(updatedSong)

                // After updating, refresh the song lists
                fetchRecentlyPlayedSongs()
            } catch (e: Exception) {
                // Handle any errors
                e.printStackTrace()
            }
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.updateSong(song)
        }
    }

    // Mark that data needs to be refreshed (call this when navigating to HomeScreen)
    fun markDataRefreshNeeded() {
        _dataRefreshNeeded.value = true
    }

    // Check if data refresh is needed
    fun isDataRefreshNeeded(): Boolean {
        return _dataRefreshNeeded.value
    }
}

package com.example.pertamaxify.data.model

import android.util.Log
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

    // Call this function when you want to refresh all data
    fun refreshAllData(email: String) {
        fetchRecentlyPlayedSongs(email)
        fetchRecentlyAddedSongs(email)
    }

    private fun fetchRecentlyPlayedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getRecentlyPlayedSongsByUser(email)
            _recentlyPlayedSongs.value = songs
        }
    }

    private fun fetchRecentlyAddedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getRecentlyAddedSongsByUser(email)
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
                fetchRecentlyPlayedSongs(email)
            } catch (e: Exception) {
                // Handle any errors
                e.printStackTrace()
            }
        }
    }

    fun increaseSongPlayCount(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSong = song.copy(
                numberOfPlay = song.numberOfPlay?.plus(1)
            )
            songRepository.updateSong(updatedSong)
            Log.d("HomeViewModel", "Play count updated for song: ${song.title} to ${updatedSong.numberOfPlay}")
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.updateSong(song)
        }
    }

    fun toggleLikeSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSong = song.copy(isLiked = !song.isLiked!!)
            songRepository.updateSong(updatedSong)
        }
    }

    fun deleteSong(song: Song, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.deleteSong(song)
            refreshAllData(email)
        }
    }
}

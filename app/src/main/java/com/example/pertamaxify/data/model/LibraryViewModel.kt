package com.example.pertamaxify.data.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.data.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: SongRepository, private val statisticRepository: StatisticRepository
) : ViewModel() {

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs

    private val _likedSongs = MutableStateFlow<List<Song>>(emptyList())
    val likedSongs: StateFlow<List<Song>> = _likedSongs

    private val _downloadedSongs = MutableStateFlow<List<Song>>(emptyList())
    val downloadedSongs: StateFlow<List<Song>> = _downloadedSongs

    fun refreshAllData(email: String) {
        fetchAllSongs(email)
        fetchLikedSongs(email)
        fetchDownloadedSongs(email)
    }

    private fun fetchAllSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _allSongs.value = repository.getAllSongsByEmail(email)
        }
    }

    private fun fetchLikedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _likedSongs.value = repository.getAllLikedSongsByEmail(email)
        }
    }

    private fun fetchDownloadedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _downloadedSongs.value = repository.getAllDownloadedSongsByEmail(email)
        }
    }

    fun saveSong(song: Song, email: String) {
        viewModelScope.launch {
            repository.upsertSong(song)
            // Refresh lists after saving
            refreshAllData(email = email)
        }
    }

    fun toggleLike(song: Song, email: String) {
        viewModelScope.launch {
            val updatedSong = song.copy(isLiked = !(song.isLiked ?: false))
            repository.updateSong(updatedSong)
            refreshAllData(email)
        }
    }

    fun deleteSong(song: Song, email: String) {
        try {
            viewModelScope.launch {
                repository.deleteSong(song)
                statisticRepository.deleteStatisticBySongId(song.id)
                refreshAllData(email)
            }
        } catch (e: Exception) {
            Log.e("LibraryViewModel", "Error deleting song: ${e.message}")
        }
    }
}
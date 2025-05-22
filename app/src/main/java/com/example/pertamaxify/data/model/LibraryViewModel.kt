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
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    // For the "All" tab
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs

    // For the "Liked" tab
    private val _likedSongs = MutableStateFlow<List<Song>>(emptyList())
    val likedSongs: StateFlow<List<Song>> = _likedSongs

    private val _downloadedSongs = MutableStateFlow<List<Song>>(emptyList())
    val downloadedSongs: StateFlow<List<Song>> = _downloadedSongs

    fun refreshAllData(email: String) {
        fetchAllSongs(email)
        fetchLikedSongs(email)
        fetchDownloadedSongs(email)
    }

    fun fetchAllSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _allSongs.value = repository.getAllSongsByEmail(email)
        }
    }

    fun fetchLikedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _likedSongs.value = repository.getAllLikedSongsByEmail(email)
        }
    }

    fun fetchDownloadedSongs(email: String) {
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
}
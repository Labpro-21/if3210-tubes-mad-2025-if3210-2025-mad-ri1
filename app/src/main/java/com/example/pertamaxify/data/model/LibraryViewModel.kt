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

    init {
        fetchAllSongs()
        fetchLikedSongs()
        fetchDownloadedSongs()
        Log.d("Songs:", allSongs.toString())
    }

    fun fetchAllSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _allSongs.value = repository.getAllSongs()
        }
    }

    fun fetchLikedSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _likedSongs.value = repository.getAllLikedSongs()
        }
    }

    fun fetchDownloadedSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _downloadedSongs.value = repository.getAllDownloadedSongs()
        }
    }

    fun saveSong(song: Song) {
        viewModelScope.launch {
            repository.upsertSong(song)
            // Refresh lists after saving
            fetchAllSongs()
            fetchLikedSongs()
            fetchDownloadedSongs()
        }
    }
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val updatedSong = song.copy(isLiked = !song.isLiked!!)
            repository.updateSong(updatedSong)
            // Reload both lists to reflect the change
            fetchAllSongs()
            fetchLikedSongs()
            fetchDownloadedSongs()
        }
    }
}
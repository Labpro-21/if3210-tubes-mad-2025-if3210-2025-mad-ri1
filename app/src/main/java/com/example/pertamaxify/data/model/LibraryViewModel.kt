package com.example.pertamaxify.data.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        fetchAllSongs()
        fetchLikedSongs()
        Log.d("Songs:", allSongs.toString())
    }

    fun fetchAllSongs() {
        viewModelScope.launch {
            _allSongs.value = repository.getAllSongs()
        }
    }

    fun fetchLikedSongs() {
        viewModelScope.launch {
            _likedSongs.value = repository.getAllLikedSongs()
        }
    }

    fun toggleLike(song: Song) {
        viewModelScope.launch {
            val updatedSong = song.copy(isLiked = !song.isLiked!!)
            repository.updateSong(updatedSong)
            // Reload both lists to reflect the change
            fetchAllSongs()
            fetchLikedSongs()
        }
    }
}
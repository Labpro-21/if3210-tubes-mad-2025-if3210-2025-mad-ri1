package com.example.pertamaxify.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _recentlyPlayedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyPlayedSongs: StateFlow<List<Song>> = _recentlyPlayedSongs

    private val _recentlyAddedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyAddedSongs: StateFlow<List<Song>> = _recentlyAddedSongs

    init {
        fetchRecentlyPlayedSongs()
        fetchRecentlyAddedSongs()
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

}

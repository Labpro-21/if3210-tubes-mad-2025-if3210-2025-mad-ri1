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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    // List of recently played songs
    var recentlyPlayedSongs by mutableStateOf<List<Song>>(emptyList())
        private set

    init {
        fetchSongs()
    }

    fun fetchSongsUser(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getAllSongsByUser(username)
            recentlyPlayedSongs = songs
        }
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            val songs = withContext(Dispatchers.IO) {
//                songRepository.getAllSongs()
            }
//            recentlyPlayedSongs = songs
        }
    }
}

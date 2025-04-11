package com.example.pertamaxify.data.model

// HomeViewModel.kt

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

    // State holder for the list of recently played songs fetched from the DB.
    var recentlyPlayedSongs by mutableStateOf<List<Song>>(emptyList())
        private set

    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            // Query the database on a background thread.
            val songs = withContext(Dispatchers.IO) {
                songRepository.getAllSongs()
            }
            recentlyPlayedSongs = songs
        }
    }
}

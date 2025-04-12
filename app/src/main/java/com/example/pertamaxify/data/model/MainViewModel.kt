package com.example.pertamaxify.data.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pertamaxify.data.repository.HistoryPlayedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// Add to your existing ViewModels package
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: HistoryPlayedRepository // Your Room DAO access
) : ViewModel() {
    private val _selectedSong = mutableStateOf<Song?>(null)
    val selectedSong: State<Song?> = _selectedSong

    init {
        loadLastPlayedSong()
    }

    fun loadLastPlayedSong() {
        viewModelScope.launch(Dispatchers.IO) { // Use IO dispatcher for DB ops
            try {
                val song = repository.getLastPlayed()
                _selectedSong.value = song
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading last played song", e)
            }
        }
    }
}
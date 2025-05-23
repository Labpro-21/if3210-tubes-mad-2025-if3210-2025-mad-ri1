package com.example.pertamaxify.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {
    val selectedSong = mutableStateOf<Song?>(null)
    val isPlayerVisible = mutableStateOf(false)

    fun updateSelectedSong(song: Song) {
        viewModelScope.launch {
            selectedSong.value = song
            isPlayerVisible.value = true
        }
    }

    fun dismissPlayer() {
        isPlayerVisible.value = false
    }
}
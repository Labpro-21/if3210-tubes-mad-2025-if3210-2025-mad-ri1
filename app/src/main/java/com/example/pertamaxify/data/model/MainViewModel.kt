package com.example.pertamaxify.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val statisticRepository: StatisticRepository,
) : ViewModel() {
    val selectedSong = mutableStateOf<Song?>(null)
    val isPlayerVisible = mutableStateOf(false)

    fun updateSelectedSong(song: Song, email: String) {
        viewModelScope.launch {
            selectedSong.value = song
            isPlayerVisible.value = true

            statisticRepository.insertStatistic(
                Statistic(
                    playedBy = email,
                    songId = song.id,
                    playedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    fun dismissPlayer() {
        isPlayerVisible.value = false
    }
}
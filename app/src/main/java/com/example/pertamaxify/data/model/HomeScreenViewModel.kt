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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val statisticRepository: StatisticRepository
) : ViewModel() {

    private val _recentlyPlayedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyPlayedSongs: StateFlow<List<Song>> = _recentlyPlayedSongs

    private val _recentlyAddedSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentlyAddedSongs: StateFlow<List<Song>> = _recentlyAddedSongs

    // Call this function when you want to refresh all data
    fun refreshAllData(email: String) {
        fetchRecentlyPlayedSongs(email)
        fetchRecentlyAddedSongs(email)
    }

    private fun fetchRecentlyPlayedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val history = statisticRepository.getAllStatisticByEmail(email).distinctBy { it.songId }
            val songIds = history.map { it.songId }
            val songs = List<Song>(songIds.size) { index ->
                songRepository.getSongById(songIds[index])
            }
            _recentlyPlayedSongs.value = songs
        }
    }

    private fun fetchRecentlyAddedSongs(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songRepository.getRecentlyAddedSongsByUser(email).distinctBy { it.id }
            _recentlyAddedSongs.value = songs
        }
    }


    fun updateSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.updateSong(song)
        }
    }

    fun toggleLikeSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSong = song.copy(isLiked = !song.isLiked!!)
            songRepository.updateSong(updatedSong)
        }
    }

    fun deleteSong(song: Song, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.deleteSong(song)
            refreshAllData(email)
        }
    }
}

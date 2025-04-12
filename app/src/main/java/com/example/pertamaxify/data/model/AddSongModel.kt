package com.example.pertamaxify.data.model

import androidx.lifecycle.ViewModel
import com.example.pertamaxify.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddSongModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {
    suspend fun addSong(song: Song) {
        songRepository.upsertSong(song)
    }


}
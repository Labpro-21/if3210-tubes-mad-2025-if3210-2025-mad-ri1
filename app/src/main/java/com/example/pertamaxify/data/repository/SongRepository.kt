// SongRepository.kt
package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.db.SongDao
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songDao: SongDao
) {
    fun getAllSongs(): List<Song> = songDao.getAllSong()

    suspend fun upsertSong(song: com.example.pertamaxify.data.model.Song) {
        songDao.upsertSong(song)
    }

    suspend fun deleteSong(song: com.example.pertamaxify.data.model.Song) {
        songDao.deleteSong(song)
    }
}

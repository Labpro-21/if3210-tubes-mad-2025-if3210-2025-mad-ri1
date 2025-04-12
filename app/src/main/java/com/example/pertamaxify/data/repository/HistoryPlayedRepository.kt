package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.HistoryPlayed
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.db.HistoryPlayedDao
import javax.inject.Inject

class HistoryPlayedRepository @Inject constructor(
    private val historyPlayedDao: HistoryPlayedDao,
    private val songRepository: SongRepository
) {
    fun getAllHistory() = historyPlayedDao.getAllHistory()

    fun getLastHistory() = historyPlayedDao.getLastHistory()

    fun getLastPlayed() : Song {
        val lastPlayed = historyPlayedDao.getLastPlayed()
        val songId = lastPlayed.songId
        return songRepository.getSongById(songId.toString())
    }

    suspend fun upsertHistory(historyPlayed: HistoryPlayed) {
        historyPlayedDao.upsertHistory(historyPlayed)
    }

    suspend fun deleteHistory(historyPlayed: HistoryPlayed) {
        historyPlayedDao.deleteHistory(historyPlayed)
    }

    suspend fun deleteById(id: Int) {
        historyPlayedDao.deleteById(id)
    }
}
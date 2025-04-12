package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.HistoryPlayed
import com.example.pertamaxify.db.HistoryPlayedDao
import javax.inject.Inject

class HistoryPlayedRepository @Inject constructor(
    private val historyPlayedDao: HistoryPlayedDao
) {
    fun getAllHistory() = historyPlayedDao.getAllHistory()

    fun getLastHistory() = historyPlayedDao.getLastHistory()

    fun getLastPlayed() = historyPlayedDao.getLastPlayed()

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
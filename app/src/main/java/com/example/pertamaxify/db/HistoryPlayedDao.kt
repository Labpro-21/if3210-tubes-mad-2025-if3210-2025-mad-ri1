package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.pertamaxify.data.model.HistoryPlayed

@Dao
interface HistoryPlayedDao {
    @Query("SELECT * FROM history")
    fun getAllHistory(): List<HistoryPlayed>

    @Query("SELECT * FROM history LIMIT 5")
    fun getLastHistory(): List<HistoryPlayed>

    @Query("SELECT * FROM history LIMIT 1")
    fun getLastPlayed(): HistoryPlayed

    @Upsert
    suspend fun upsertHistory(historyPlayed: HistoryPlayed)

    @Delete
    suspend fun deleteHistory(historyPlayed: HistoryPlayed)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int)
}
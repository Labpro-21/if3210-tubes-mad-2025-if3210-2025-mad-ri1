package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.pertamaxify.data.model.Statistic

@Dao
interface StatisticDao {
    @Insert
    suspend fun insert(statistic: Statistic)

    @Upsert
    suspend fun upsert(statistic: Statistic)

    @Query("SELECT * FROM statistic")
    suspend fun getAllStatistic(): List<Statistic>

    @Query("SELECT * FROM statistic WHERE playedBy = :email")
    suspend fun getAllStatisticByEmail(email: String): List<Statistic>

    @Query("SELECT * FROM statistic WHERE songId = :songId")
    suspend fun getAllStatisticBySongId(songId: Int): List<Statistic>

    @Delete
    suspend fun delete(statistic: Statistic)

    @Update
    suspend fun update(statistic: Statistic)

    @Query("DELETE FROM statistic WHERE playedBy = :email AND songId = :songId")
    suspend fun deleteAll(email: String, songId: Int)

    @Query("DELETE FROM statistic WHERE playedBy = :email AND songId = :songId")
    suspend fun deleteStatistic(email: String, songId: Int)

    @Query("DELETE FROM statistic WHERE songId = :songId")
    suspend fun deleteStatisticBySongId(songId: Int)

    suspend fun getNumberOfPlaySong(email: String, songId: Int): Int {
        val statistics = getAllStatisticByEmail(email)
        return statistics.filter { it.songId == songId }.size
    }

    suspend fun getTotalPlaysByEmail(email: String): Int {
        val statistics = getAllStatisticByEmail(email)
        return statistics.size
    }

    suspend fun getMostPlayedSong(email: String): Statistic? {
        val statistics = getAllStatisticByEmail(email)
        return statistics.groupBy { it.songId }.maxByOrNull { it.value.size }?.value?.firstOrNull()
    }

    suspend fun getListOfMostPlayedSong(email: String, limit: Int): List<Statistic> {
        val statistics = getAllStatisticByEmail(email)
        return statistics.groupBy { it.songId }.map { it.value.first() }
            .sortedByDescending { it.playedAt }.take(limit)
    }

    suspend fun getTotalListeningTime(email: String): Long {
        val statistics = getAllStatisticByEmail(email)
        return statistics.sumOf { it.playedAt }
    }

    suspend fun getLastPlayedSongByDays(email: String, days: Int): List<Statistic> {
        val statistics = getAllStatisticByEmail(email)
        val currentTime = System.currentTimeMillis()
        return statistics.filter { (currentTime - it.playedAt) <= days * 24 * 60 * 60 * 1000 }
    }
}
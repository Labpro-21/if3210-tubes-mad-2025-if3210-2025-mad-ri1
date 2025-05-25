package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.Statistic
import com.example.pertamaxify.db.StatisticDao
import javax.inject.Inject

class StatisticRepository @Inject constructor(
    private val statisticDao: StatisticDao
) {
    suspend fun upsertStatistic(statistic: Statistic) {
        statisticDao.upsert(statistic)
    }

    suspend fun deleteStatistic(statistic: Statistic) {
        statisticDao.delete(statistic)
    }

    suspend fun updateStatistic(statistic: Statistic) {
        statisticDao.update(statistic)
    }

    suspend fun insertStatistic(statistic: Statistic) {
        statisticDao.insert(statistic)
    }

    suspend fun getAllStatistic(): List<Statistic> {
        return statisticDao.getAllStatistic()
    }

    suspend fun getAllStatisticByEmail(email: String): List<Statistic> {
        return statisticDao.getAllStatisticByEmail(email)
    }

    suspend fun getAllStatisticBySongId(songId: Int): List<Statistic> {
        return statisticDao.getAllStatisticBySongId(songId)
    }

    suspend fun deleteAll(email: String, songId: Int) {
        statisticDao.deleteAll(email, songId)
    }

    suspend fun deleteStatistic(email: String, songId: Int) {
        statisticDao.deleteStatistic(email, songId)
    }

    suspend fun getNumberOfPlaySong(email: String, songId: Int): Int {
        return statisticDao.getNumberOfPlaySong(email, songId)
    }

    suspend fun getTotalPlaysByEmail(email: String): Int {
        return statisticDao.getTotalPlaysByEmail(email)
    }

    suspend fun getMostPlayedSong(email: String): Statistic? {
        return statisticDao.getMostPlayedSong(email)
    }

    suspend fun getListOfMostPlayedSong(email: String, limit: Int): List<Statistic> {
        return statisticDao.getListOfMostPlayedSong(email, limit)
    }

    suspend fun getTotalListeningTime(email: String): Long {
        return statisticDao.getTotalListeningTime(email)
    }

    suspend fun getLastPlayedSongByDays(email: String, days: Int): List<Statistic> {
        return statisticDao.getLastPlayedSongByDays(email, days)
    }

    suspend fun getUniqueRecentlyPlayedSongIds(email: String, limit: Int = 20): List<Int> {
        val statistics = statisticDao.getAllStatisticByEmail(email)
            .sortedByDescending { it.playedAt }

        return statistics
            .distinctBy { it.songId }
            .take(limit)
            .map { it.songId }
    }

    suspend fun getUniqueSongsPlayedOnStreak(email: String, streakDays: Int): List<Int> {
        val statistics = statisticDao.getAllStatisticByEmail(email)
        val groupedBySong = statistics.groupBy { it.songId }

        val songsOnStreak = mutableListOf<Int>()
        val millisInDay = 24 * 60 * 60 * 1000

        groupedBySong.forEach { (songId, plays) ->
            val playDates = plays.map {
                it.playedAt / millisInDay
            }.toSet()

            var streak = 0
            val currentDay = System.currentTimeMillis() / millisInDay

            while (streak < streakDays && playDates.contains(currentDay - streak)) {
                streak++
            }

            if (streak >= streakDays) {
                songsOnStreak.add(songId)
            }
        }

        return songsOnStreak
    }

}
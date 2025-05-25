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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

data class MonthlyStats(
    val monthYear: String,
    val timesListened: Int,
    val topSong: String,
    val topArtist: String,
    val songImage: String?,
    val artistImage: String? = null,
    val streakSong: Song? = null,
    val streakDay: Int? = null,
    val streakStartDate: LocalDate? = null,
    val streakEndDate: LocalDate? = null,
    val top5Songs: List<Pair<Song, Int>> = emptyList(), // Song + times played
    val top5Artists: List<Pair<String, Int>> = emptyList(), // Artist name + times played
    val top5ArtistsImage: List<Pair<String, String?>> = emptyList() // Artist name + random song artwork
)


data class StreakInfo(
    val days: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

const val MONTHLY_STATS_LIMIT = 2

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val statisticRepository: StatisticRepository,
    private val songRepository: SongRepository
): ViewModel() {

    private val _monthlyStats = MutableStateFlow(emptyList<MonthlyStats>())
    val monthlyStats: StateFlow<List<MonthlyStats>> = _monthlyStats

    fun fetchAllStats(email: String) {
       fetchMonthlyStats(email)
    }

    private fun fetchMonthlyStats(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = LocalDate.now()
            val stats = mutableListOf<MonthlyStats>()
            repeat(MONTHLY_STATS_LIMIT) { offset ->
                val target = now.minusMonths(offset.toLong())
                val year = target.year
                val month = target.monthValue

                val plays = statisticRepository
                    .getAllStatisticByEmail(email)
                    .filter { stat ->
                        Instant.ofEpochMilli(stat.playedAt)
                            .atZone(ZoneId.systemDefault())
                            .let { it.year == year && it.monthValue == month }
                    }

                Log.d("StatisticViewModel", "Plays for $year-${month.toString().padStart(2, '0')}: ${plays.size}")

                val userSongsById: Map<Int, Song> = songRepository
                    .getAllSongsByEmail(email)
                    .associateBy { it.id }

                val totalSeconds = plays.sumOf { stat ->
                    userSongsById[stat.songId]?.duration ?: 0
                }
                val minutesListened = (totalSeconds / 60.0).roundToInt()

                Log.d("StatisticViewModel", "Minutes listened for $year-${month.toString().padStart(2, '0')}: $minutesListened")

                val topSongId = plays
                    .groupingBy { it.songId }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key

                val songPlayCounts = plays.groupingBy { it.songId }.eachCount()
                val top5Songs = songPlayCounts.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .mapNotNull { entry -> userSongsById[entry.key]?.let { it to entry.value } }

                val artistPlayCounts = plays.mapNotNull { userSongsById[it.songId]?.artist }
                    .groupingBy { it }
                    .eachCount()
                val top5Artists = artistPlayCounts.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .map { it.toPair() }

                val topSongName = top5Songs.firstOrNull()?.first?.title ?: "—"
                val topArtistName = top5Artists.firstOrNull()?.first ?: "—"


                val songByArtist = songRepository
                    .getAllSongsByArtist(topArtistName)
                    .takeIf { it.isNotEmpty() }
                    ?.random()

                val top5ArtistsImage = top5Artists.map { (artist, _) ->
                    val artistSongs = songRepository.getAllSongsByArtist(artist)
                    val randomArtwork = artistSongs
                        .mapNotNull { it.artwork }
                        .filter { it.isNotBlank() }
                        .randomOrNull()
                    artist to randomArtwork // This will be null if no valid image, handled by UI
                }


                val streaks = plays.groupBy { it.songId }
                    .mapValues { (_, entries) ->
                        val dates = entries.map {
                            Instant.ofEpochMilli(it.playedAt)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }.distinct().sorted() // Use distinct, sorted dates
                        findLongestStreakInfo(dates) // Use the new function
                    }

                val topStreakEntry = streaks.maxByOrNull { it.value.days }

                val streakSongId = topStreakEntry?.key
                val streakInfo = topStreakEntry?.value

                Log.d("StatisticViewModel", "Streak for $year-${month.toString().padStart(2, '0')}: " +
                        "${streakInfo?.days ?: 0} days from ${streakInfo?.startDate} to ${streakInfo?.endDate}")

                stats += MonthlyStats(
                    monthYear = "${target.month.name.take(3)} $year",
                    timesListened = minutesListened,
                    topSong = topSongName,
                    topArtist = topArtistName,
                    songImage = topSongId?.let { userSongsById[it]?.artwork }, // Optimized
                    artistImage = songByArtist?.artwork,
                    streakSong = streakSongId?.let { userSongsById[it] }, // Optimized
                    streakDay = streakInfo?.days,
                    streakStartDate = streakInfo?.startDate,
                    streakEndDate = streakInfo?.endDate,
                    top5Songs = top5Songs,
                    top5Artists = top5Artists,
                    top5ArtistsImage = top5ArtistsImage
                )
            }
            _monthlyStats.value = stats
        }
    }

    private fun findLongestStreakInfo(dates: List<LocalDate>): StreakInfo {
        if (dates.isEmpty()) {
            return StreakInfo(0, null, null)
        }

        var longestStreak = 0
        var longestStreakEndDate: LocalDate? = null

        var currentStreak = 1

        for (i in 1 until dates.size) {
            if (dates[i-1].plusDays(1) == dates[i]) {
                currentStreak++
            } else {
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                    longestStreakEndDate = dates[i-1]
                }
                // Reset
                currentStreak = 1
            }
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak
            longestStreakEndDate = dates.last()
        }

        return if (longestStreak > 0 && longestStreakEndDate != null) {
            val startDate = longestStreakEndDate.minusDays(longestStreak.toLong() - 1)
            StreakInfo(longestStreak, startDate, longestStreakEndDate)
        } else {
            StreakInfo(if(dates.isNotEmpty()) 1 else 0, null, null)
        }
    }

}
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
    val streakEndDate: LocalDate? = null
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

                val topSongName = topSongId?.let { songRepository.getSongById(it).title } ?: "—"

                val topArtistName = topSongId
                        ?.let { songRepository.getSongById(it).artist }
                        ?: "—"


                val songByArtist = songRepository
                    .getAllSongsByArtist(topArtistName)
                    .takeIf { it.isNotEmpty() }
                    ?.random()

                val streaks = plays.groupBy { it.songId }
                    .mapValues { (_, entries) ->
                        val dates = entries.map {
                            Instant.ofEpochMilli(it.playedAt)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        getLongestStreakDays(dates)
                    }

                val topStreakEntry = streaks.maxByOrNull { it.value }
                val streakSongId = topStreakEntry?.key
                val streakDays = topStreakEntry?.value ?: 0

                stats += MonthlyStats(
                    monthYear = "${target.month.name.take(3)} $year",
                    timesListened = minutesListened,
                    topSong = topSongName,
                    topArtist = topArtistName,
                    songImage = topSongId?.let { songRepository.getSongById(it).artwork },
                    artistImage = songByArtist?.artwork,
                    streakSong = streakSongId?.let { songRepository.getSongById(it) },
                    streakDay = streakDays
                )
            }
            _monthlyStats.value = stats
        }
    }

    private fun getLongestStreakDays(dates: List<LocalDate>): Int {
        val uniqueDates = dates.toSet().sorted()
        var longest = 0
        var current = 1

        for (i in 1 until uniqueDates.size) {
            if (uniqueDates[i - 1].plusDays(1) == uniqueDates[i]) {
                current++
            } else {
                longest = maxOf(longest, current)
                current = 1
            }
        }

        return maxOf(longest, current)
    }

}
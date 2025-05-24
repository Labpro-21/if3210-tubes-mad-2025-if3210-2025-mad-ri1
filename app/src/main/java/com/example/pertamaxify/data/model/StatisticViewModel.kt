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
    val topGenre: String
)

const val MONTHLY_STATS_LIMIT = 2

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val statisticRepository: StatisticRepository,
    private val songRepository: SongRepository
): ViewModel() {

    private val _monthlyStats = MutableStateFlow<List<MonthlyStats>>(emptyList<MonthlyStats>())
    val monthlyStats: StateFlow<List<MonthlyStats>> = _monthlyStats

    fun fetchAllStats(email: String) {
       fetchMonthlyStats(email)
    }

    fun fetchMonthlyStats(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = LocalDate.now()
            val stats = mutableListOf<MonthlyStats>()
            repeat(MONTHLY_STATS_LIMIT) { offset ->
                val target = now.minusMonths(offset.toLong())
                val year = target.year
                val month = target.monthValue
                // all plays in that year/month
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
                    // Lookup the song once, get its duration (or 0 if missing)
                    userSongsById[stat.songId]?.duration ?: 0
                }
                val minutesListened = (totalSeconds / 60.0).roundToInt()

                Log.d("StatisticViewModel", "Minutes listened for $year-${month.toString().padStart(2, '0')}: $minutesListened")

                // top song = most frequent songId in plays
                val topSongId = plays
                    .groupingBy { it.songId }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key

                val topSongName = topSongId?.let { songRepository.getSongById(it).title } ?: "—"


                // top artist = artist of that top song
                val topArtistName = topSongId
                        ?.let { songRepository.getSongById(it).artist }
                        ?: "—"


                stats += MonthlyStats(
                    monthYear = "${target.month.name.take(3)} $year",
                    timesListened = minutesListened,
                    topSong = topSongName,
                    topArtist = topArtistName,
                    topGenre = "" // you can extend if you track genres
                )
            }
            _monthlyStats.value = stats
        }
    }
}
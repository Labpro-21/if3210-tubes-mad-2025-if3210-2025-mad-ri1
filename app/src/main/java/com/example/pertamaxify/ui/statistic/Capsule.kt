package com.example.pertamaxify.ui.statistic

import CsvExporter.writeCsvToUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.StatisticViewModel
import com.example.pertamaxify.ui.theme.Typography
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun Capsule(
    email: String? = null,
    statisticViewModel: StatisticViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(email) {
        if (!email.isNullOrBlank()) {
            statisticViewModel.fetchAllStats(email)
        }
    }

    val stats by statisticViewModel.monthlyStats.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                writeCsvToUri(context, uri, stats)
            }
        }
    )

    Column {
        Row {
            Text("Your Sound Capsule", style = Typography.titleLarge)
            IconButton(
                onClick = {
                    val defaultFileName = "sound_capsule_${System.currentTimeMillis()}.csv"
                    launcher.launch(defaultFileName)
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download), // You may need to import another icon
                    contentDescription = "Download",
                    tint = Color.White
                )
            }

        }
        Spacer(Modifier.height(8.dp))
        if (stats.isEmpty()) {
            Text(
                text = "No statistics available. Please listen to some music first.",
                style = Typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            stats.forEach { monthStat ->
                MonthlyStatsScreen(
                    monthYear          = monthStat.monthYear,
                    minutesListened    = monthStat.timesListened,
                    topArtistName      = monthStat.topArtist,
                    topArtistImageUrl  = monthStat.artistImage ?: "",
                    topSongName        = monthStat.topSong,
                    topSongImageUrl    = monthStat.songImage ?: "",
                    streakDays         = monthStat.streakDay ?: 0,
                    streakSong         = monthStat.streakSong,
                    streakStart        = monthStat.streakStartDate,
                    streakEnd          = monthStat.streakEndDate,
                )
            }
        }
    }
}

@Composable
fun MonthlyStatsScreen(
    monthYear: String,
    minutesListened: Int,
    topArtistName: String,
    topArtistImageUrl: String,
    topSongName: String,
    topSongImageUrl: String,
    streakDays: Int,
    streakSong: Song? = null,
    streakStart: LocalDate? = null,
    streakEnd: LocalDate? = null,
    onShareClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = monthYear,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Time listened", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$minutesListened minutes",
                        style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Top artist", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = topArtistImageUrl,
                        contentDescription = "Top artist",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(topArtistName, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Top song", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = topSongImageUrl,
                    contentDescription = "Top song",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.height(8.dp))
                Text(topSongName, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
        }

        Spacer(Modifier.height(24.dp))

        if (streakDays > 1 && streakStart != null && streakEnd != null && streakSong != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    Box  {
                        AsyncImage(
                            model = streakSong.artwork,
                            contentDescription = "Streak cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "You had a $streakDays-day streak",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "You played ${streakSong.title} by ${streakSong.artist.trim()} day after day. You were on fire.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    val dateRange = if (streakStart.monthValue == streakEnd.monthValue) {
                        val formatter = DateTimeFormatter.ofPattern("MMM d")
                        "${streakStart.format(formatter)}–${streakEnd.dayOfMonth}, ${streakEnd.year}"
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("MMM d")
                        "${streakStart.format(formatter)}–${streakEnd.format(formatter)}, ${streakEnd.year}"
                    }

                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

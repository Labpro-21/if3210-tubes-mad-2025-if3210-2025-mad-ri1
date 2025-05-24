package com.example.pertamaxify.ui.statistic

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pertamaxify.data.model.StatisticViewModel
import com.example.pertamaxify.ui.theme.Typography

@Preview
@Composable
fun Capsule(
    email: String? = null,
    statisticViewModel: StatisticViewModel = hiltViewModel()
) {

    LaunchedEffect(email) {
        if (!email.isNullOrBlank()) {
            statisticViewModel.fetchAllStats(email)
        }
    }

    val stats by statisticViewModel.monthlyStats.collectAsState()

    Column {
        Text("Your Sound Capsule", style = Typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        stats.forEach { monthStat ->
            MonthlyStatsScreen(
                monthYear          = monthStat.monthYear,
                minutesListened    = monthStat.timesListened,
                topArtistName      = monthStat.topArtist,
                topArtistImageUrl  = "",
                topSongName        = monthStat.topSong,
                topSongImageUrl    = "",
                streakDays         = 0,    // omit streak or compute similarly
                streakSongTitle    = "",
                streakSongArtist   = "",
                streakCoverUrl     = "",
            ) {
                // onClick: maybe drill in to detail
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
    streakSongTitle: String,
    streakSongArtist: String,
    streakCoverUrl: String,
    onShareClick: () -> Unit = {},
    onClick: () -> Unit = {  }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = monthYear,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
        )

        // Time Listened + Top artist / Top song row
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Time listened card
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

            // Top artist
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

        // Top song (full width, under the row)
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

        // Streak card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                Box  {
                    AsyncImage(
                        model = streakCoverUrl,
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
                    text = "You played $streakSongTitle by $streakSongArtist day after day. You were on fire.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = "Mar 21–25, 2025",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.End)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

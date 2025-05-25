package com.example.pertamaxify.ui.statistic

import com.example.pertamaxify.utils.StatisticExporter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.StatisticViewModel
import com.example.pertamaxify.ui.theme.Typography
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                StatisticExporter.writeCsvToUri(context, uri, stats)
            }
        }
    )

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            if (uri != null) {
                StatisticExporter.writePdfToUri(context, uri, stats)
            }
        }
    )


    var selectedTopSongs by remember {mutableStateOf<List<Pair<Song, Int>>?>(null)}
    var selectedTopArtists by remember {mutableStateOf<List<Pair<String, Int>>?>(null) }
    var artistsImage by remember { mutableStateOf<List<Pair<String, String?>>>(emptyList()) }

    Column {
        Row {
            Text("Your Sound Capsule", style = Typography.titleLarge, color = Color.White)
            IconButton(
                onClick = {
                    val defaultFileName = "sound_capsule_${System.currentTimeMillis()}.csv"
                    csvExportLauncher.launch(defaultFileName)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.csv_logo),
                    contentDescription = "Download CSV",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = {
                    val pdfFileName = "sound_capsule_${System.currentTimeMillis()}.pdf"
                    pdfExportLauncher.launch(pdfFileName)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.picture_as_pdf),
                    contentDescription = "Download PDF",
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        if (stats.isEmpty()) {
            Text(
                text = "No statistics available. Please listen to some music first.",
                style = Typography.bodyMedium,
                color = Color.White,
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
                    onTopSongClick = {
                        selectedTopSongs = monthStat.top5Songs.takeIf { it.isNotEmpty() }
                        artistsImage = monthStat.top5ArtistsImage
                        selectedTopArtists = null
                    },
                    onTopArtistClick = {
                        selectedTopArtists = monthStat.top5Artists.takeIf { it.isNotEmpty() }
                        selectedTopSongs = null
                    }
                )
            }
        }

        if (selectedTopSongs != null) {
            TopSongsDialog(
                songs = selectedTopSongs!!,
                onDismiss = { selectedTopSongs = null }
            )
        }
        if (selectedTopArtists != null) {
            TopArtistsDialog(
                artists = selectedTopArtists!!,
                artistsImage = artistsImage,
                onDismiss = { selectedTopArtists = null }
            )
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
    onTopSongClick: () -> Unit = {},
    onTopArtistClick: () -> Unit = {},
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
            color = Color.White,
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
                modifier = Modifier.weight(1f).clickable { onTopArtistClick() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Top Artist", style = MaterialTheme.typography.bodySmall)
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
                .padding(horizontal = 16.dp)
                .clickable { onTopSongClick() }
            ,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Top Song", style = MaterialTheme.typography.bodySmall)
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

@Composable
fun TopSongsDialog(
    songs: List<Pair<Song, Int>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top 5 Songs", style = Typography.titleMedium) },
        text = {
            Column {
                songs.forEach { (song, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = song.artwork,
                            contentDescription = song.title,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(song.title, style = Typography.bodyMedium)
                            Text("Played $count times", style = Typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun TopArtistsDialog(
    artists: List<Pair<String, Int>>,
    artistsImage: List<Pair<String, String?>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top 5 Artists", style = Typography.titleMedium) },
        text = {
            Column {
                artists.forEach { (artist, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (artistsImage[artists.indexOfFirst { it.first == artist }].second != null) {
                            AsyncImage(
                                model = artistsImage[artists.indexOfFirst { it.first == artist }].second,
                                contentDescription = artist,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = "Artist icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(artist, style = Typography.bodyMedium)
                            Text("Played $count times", style = Typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


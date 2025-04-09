package com.example.pertamaxify.ui.song

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText

@Composable
fun LocalExternalImage(fileName: String) {
    AsyncImage(
        model = fileName.toUri(),
        contentDescription = null,
        modifier = Modifier
            .width(100.dp),
    )
}

@Composable
fun NewSongsSection(songs: List<Song>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("New songs", style = Typography.displayLarge, color = WhiteText)
        LazyRow {
            items(songs) { song ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .padding(8.dp)
                ) {
                    LocalExternalImage(song.image)
                    Text(song.title, color = WhiteText, maxLines = 1)
                    Text(song.singer, color = Color.Gray, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedSection(songs: List<Song>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recently played", style = MaterialTheme.typography.titleLarge, color = WhiteText)
        Column {
            songs.forEach { song ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    LocalExternalImage(song.image)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(song.title, color = Color.White)
                        Text(song.singer, color = Color.Gray)
                    }
                }
            }
        }
    }
}
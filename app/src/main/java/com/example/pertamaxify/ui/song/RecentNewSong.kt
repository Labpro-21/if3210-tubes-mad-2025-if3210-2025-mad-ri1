package com.example.pertamaxify.ui.song

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.WhiteText

@Composable
fun NewSongsSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onToggleLike: ((Song) -> Unit)? = null,
    onDeleteSong: ((Song) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp)) {
        Text("New songs", style = MaterialTheme.typography.titleLarge, color = WhiteText, modifier = Modifier.padding(0.dp, 4.dp))
        LazyRow {
            items(songs) { song ->
                SongItem(
                    song = song,
                    onSongClick = onSongClick,
                    type = "horizontal",
                    onToggleLike = onToggleLike,
                    onDeleteSong = onDeleteSong
                )
            }
        }
    }
}

@Composable
fun RecentlyPlayedSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onToggleLike: ((Song) -> Unit)? = null,
    onDeleteSong: ((Song) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recently played", style = MaterialTheme.typography.titleLarge, color = WhiteText, modifier = Modifier.padding(0.dp, 4.dp))
        Column {
            songs.forEach { song ->
                SongItem(
                    song = song,
                    onSongClick = onSongClick,
                    type = "vertical",
                    onToggleLike = onToggleLike,
                    onDeleteSong = onDeleteSong
                )
            }
        }
    }
}

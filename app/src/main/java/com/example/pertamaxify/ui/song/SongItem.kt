package com.example.pertamaxify.ui.song

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(song: Song, onSongClick: (Song) -> Unit, type: String? = "vertical") {
    if (type == "horizontal") {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(8.dp)
                .clickable { onSongClick(song) } // clickable here
        ) {
            if (song.artwork.isNullOrBlank()) {
                AsyncImage(
                    model = R.drawable.song_image_placeholder,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            } else {
                AsyncImage(
                    model = song.artwork,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            }
            Text(song.title, color = WhiteText, maxLines = 1)
            Text(song.artist, color = Color.Gray, maxLines = 1)
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSongClick(song) } // Added clickable here
                .padding(vertical = 8.dp)
        ) {
            if (song.artwork.isNullOrBlank()) {
                AsyncImage(
                    model = R.drawable.song_image_placeholder,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            } else {
                AsyncImage(
                    model = song.artwork,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(song.title, color = WhiteText)
                Text(song.artist, color = Color.Gray)
            }
        }
    }
}
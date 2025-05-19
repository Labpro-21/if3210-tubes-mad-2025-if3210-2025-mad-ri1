package com.example.pertamaxify.ui.song

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    onSongClick: (Song) -> Unit,
    type: String? = "vertical",
    onToggleLike: ((Song) -> Unit)? = null,
    onDeleteSong: ((Song) -> Unit)? = null
) {
    // State for context menu
    var showContextMenu by remember { mutableStateOf(false) }

    if (type == "horizontal") {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(8.dp)
                .combinedClickable(
                    onClick = { onSongClick(song) },
                    onLongClick = { showContextMenu = true }
                )
        ) {
            // Show context menu if long-pressed
            ContextMenu(
                showContextMenu = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                song = song,
                onToggleLike = onToggleLike,
                onDeleteSong = onDeleteSong
            )

            if (song.artwork.isNullOrBlank()) {
                // Use local resource for placeholder
                Image(
                    painter = painterResource(id = R.drawable.song_image_placeholder),
                    contentDescription = "Song artwork",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            } else {
                AsyncImage(
                    model = song.artwork,
                    contentDescription = "Song artwork",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    error = painterResource(id = R.drawable.song_image_placeholder)
                )
            }
            Text(song.title, color = WhiteText, maxLines = 1)
            Text(song.artist, color = Color.Gray, maxLines = 1)
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onSongClick(song) },
                    onLongClick = { showContextMenu = true }
                )
                .padding(vertical = 8.dp)
        ) {
            // Show context menu if long-pressed
            ContextMenu(
                showContextMenu = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                song = song,
                onToggleLike = onToggleLike,
                onDeleteSong = onDeleteSong
            )

            if (song.artwork.isNullOrBlank()) {
                // Use local resource for placeholder
                Image(
                    painter = painterResource(id = R.drawable.song_image_placeholder),
                    contentDescription = "Song artwork",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
            } else {
                AsyncImage(
                    model = song.artwork,
                    contentDescription = "Song artwork",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    error = painterResource(id = R.drawable.song_image_placeholder)
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

@Composable
fun ContextMenu(
    showContextMenu: Boolean,
    onDismissRequest: () -> Unit,
    song: Song,
    onToggleLike: ((Song) -> Unit)? = null,
    onDeleteSong: ((Song) -> Unit)? = null
) {
    DropdownMenu(
        expanded = showContextMenu,
        onDismissRequest = onDismissRequest
    ) {
        // Add to favorites / Remove from favorites
        onToggleLike?.let {
            DropdownMenuItem(
                text = {
                    Row {
                        Icon(
                            painter = painterResource(
                                id = if (song.isLiked == true) R.drawable.tabler_heart_filled
                                else R.drawable.tabler_heart
                            ),
                            contentDescription = null,
                            tint = if (song.isLiked == true) Color(0xFFFF80AB) else Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = if (song.isLiked == true) "Remove from favorites"
                            else "Add to favorites"
                        )
                    }
                },
                onClick = {
                    it(song)
                    onDismissRequest()
                }
            )
        }

        // Delete song option
        onDeleteSong?.let {
            DropdownMenuItem(
                text = {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Delete song")
                    }
                },
                onClick = {
                    it(song)
                    onDismissRequest()
                }
            )
        }
    }
}

package com.example.pertamaxify.ui.song

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.services.DownloaderService
import com.example.pertamaxify.ui.theme.WhiteText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnlineSongItem(
    song: SongResponse,
    onSongClick: (SongResponse) -> Unit,
    type: String = "vertical",
    rank: Int? = null,
    onDownload: ((SongResponse) -> Unit)? = null,
    email: String? = null,
) {
    val context = LocalContext.current
    var showContextMenu by remember { mutableStateOf(false) }

    if (type == "horizontal") {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(8.dp)
                .combinedClickable(
                    onClick = { onSongClick(song) },
                    onLongClick = { showContextMenu = true })) {
            OnlineSongContextMenu(
                showContextMenu = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                song = song,
                onDownload = {
                    if (onDownload != null) {
                        onDownload(song)
                    } else {
                        DownloaderService.startDownload(context, song, email)
                    }
                })

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(song.artwork)
                    .crossfade(true).build(),
                contentDescription = "Song artwork",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                error = painterResource(id = R.drawable.song_image_placeholder)
            )

            Text(
                text = song.title, color = WhiteText, maxLines = 1, overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (rank != null) {
                Text(
                    text = "#$rank", color = Color(0xFF1DB954), maxLines = 1
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onSongClick(song) },
                    onLongClick = { showContextMenu = true })
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OnlineSongContextMenu(
                showContextMenu = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                song = song,
                onDownload = {
                    if (onDownload != null) {
                        onDownload(song)
                    } else {
                        DownloaderService.startDownload(context, song, email)
                    }
                })

            if (rank != null) {
                Text(
                    text = "$rank", color = Color(0xFF1DB954), modifier = Modifier.width(32.dp)
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(song.artwork)
                    .crossfade(true).build(),
                contentDescription = "Song artwork",
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                error = painterResource(id = R.drawable.song_image_placeholder)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    color = WhiteText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = song.duration, color = Color.Gray, modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun OnlineSongContextMenu(
    showContextMenu: Boolean,
    onDismissRequest: () -> Unit,
    song: SongResponse,
    onDownload: () -> Unit
) {
    DropdownMenu(
        expanded = showContextMenu, onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(text = {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Download song")
            }
        }, onClick = {
            onDownload()
            onDismissRequest()
        })
        // Share song option
        DropdownMenuItem(text = {
            Row {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_share),
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
                Log.d(
                    "OnlineSongItem", "Sharing song: ${song.title} with server ID: ${song.id}"
                )
                Text("Share song")
            }
        }, onClick = {
            // Share function
            onDismissRequest()
        })
    }
}
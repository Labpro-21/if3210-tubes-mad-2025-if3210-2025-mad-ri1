package com.example.pertamaxify.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.WhiteText
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import com.example.pertamaxify.R

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


@Composable
fun MusicPlayerScreen(song: Song) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(song.audioPath)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    LaunchedEffect(player) {
        while (true) {
            currentPosition = player.currentPosition
            duration = player.duration.takeIf { it > 0 } ?: duration
            delay(500L)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = song.imagePath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(song.title, style = MaterialTheme.typography.headlineMedium, color = WhiteText)
        Text(song.singer, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = currentPosition.coerceAtMost(duration).toFloat(),
            onValueChange = {
                player.seekTo(it.toLong())
            },
            valueRange = 0f..(duration.takeIf { it > 0 }?.toFloat() ?: 1f),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatDuration(currentPosition), color = WhiteText)
            Text(formatDuration(duration), color = WhiteText)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* prev song */ }) {
                Icon(
                    painter = painterResource((R.drawable.skip_prev)),
                    contentDescription = null, tint = WhiteText,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = {
                isPlaying = !isPlaying
                if (isPlaying) player.play() else player.pause()
            }) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(R.drawable.pause),
                        contentDescription = "Pause",
                        tint = WhiteText,
                        modifier = Modifier.size(48.dp)
                    )
                }
                else {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = WhiteText,
                        modifier = Modifier.size(48.dp)
                    )
                }

            }

            IconButton(onClick = { /* next song logic */ }) {
                Icon(
                    painter = painterResource(R.drawable.skip_next),
                    contentDescription = null, tint = WhiteText,
                    modifier = Modifier.size(40.dp))
            }
        }
    }
}

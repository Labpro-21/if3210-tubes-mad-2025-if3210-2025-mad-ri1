package com.example.pertamaxify.ui.player

import androidx.compose.foundation.LocalIndication
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.player.MusicPlayerManager
import com.example.pertamaxify.ui.theme.RedBackground
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText
import kotlinx.coroutines.delay

@Composable
fun MiniPlayer(
    song: Song?,
    musicPlayerManager: MusicPlayerManager,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val isPlaying by musicPlayerManager.isPlaying.collectAsState()
    val currentPosition by musicPlayerManager.currentPosition.collectAsState()
    val duration by musicPlayerManager.duration.collectAsState()
    val currentSong by musicPlayerManager.currentSong.collectAsState()

    val displaySong = currentSong ?: song ?: Song(
        id = 0,
        title = "No song selected",
        artist = "",
        artwork = "",
        url = "",
    )

    var progress by remember { mutableFloatStateOf(0f) }
    var isLiked by remember { mutableStateOf(displaySong.isLiked ?: false) }

    LaunchedEffect(currentPosition, duration) {
        if (duration > 0L) {
            progress = (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(song) {
        song?.let {
            if (currentSong?.id != it.id) {
                musicPlayerManager.playSong(it)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = RedBackground.copy(alpha = 0.56f)
            )
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(6.dp)) {
            Row(Modifier.align(Alignment.CenterStart)) {
                if (displaySong.artwork.isNullOrBlank()) {
                    AsyncImage(
                        model = R.drawable.song_image_placeholder,
                        contentDescription = "Album Art",
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                        placeholder = painterResource(id = R.drawable.logo)
                    )
                } else {
                    AsyncImage(
                        model = displaySong.artwork,
                        contentDescription = "Album Art",
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                        placeholder = painterResource(id = R.drawable.logo)
                    )
                }

                Column(Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        text = displaySong.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = WhiteText,
                        maxLines = 1,
                    )
                    Text(
                        text = displaySong.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                    )
                }
            }

            Row(Modifier.align(Alignment.CenterEnd)) {
                IconButton(
                    onClick = { musicPlayerManager.playPause() },
                    modifier = Modifier.size(40.dp)
                ) {
                    if (isPlaying) {
                        Icon(
                            painter = painterResource(R.drawable.pause),
                            contentDescription = "Pause",
                            tint = WhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = WhiteText,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            color = WhiteText,
            trackColor = WhiteHint,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter)
        )
    }
}
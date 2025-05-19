package com.example.pertamaxify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.font.FontWeight
import com.example.pertamaxify.R
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.ui.theme.RedBackground
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.utils.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    song: Song,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    email: String? = null,
    songRepository: SongRepository? = null // Optional repository injection
) {
    val context = LocalContext.current

    // Get user email if not provided
    val userEmail = email ?: remember {
        val accessToken = SecurePrefs.getAccessToken(context)
        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val username = jwtPayload?.username ?: ""
            if (username.isNotEmpty()) "$username@std.stei.itb.ac.id" else ""
        } else {
            ""
        }
    }

    // Update recently played timestamp
    LaunchedEffect(song.id) {
        if (songRepository != null && userEmail.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val updatedSong = song.copy(recentlyPlayed = Date())
                songRepository.updateSong(updatedSong)
            }
        }
    }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(song.url)
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

    // Add dismiss button and apply the passed modifier
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RedBackground)
                .padding(16.dp)
        ) {
            // Dismiss button at top-right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,  // Use the passed dismiss callback
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close player",
                        tint = WhiteText,
                    )
                }
            }

            if (song.artwork.isNullOrBlank()) {
                // Placeholder for artwork if not available
                AsyncImage(
                    model = R.drawable.song_image_placeholder,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            else {
                // Load the song artwork
                AsyncImage(
                    model = song.artwork,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                song.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = WhiteText
            )
            Text(
                song.artist,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = WhiteHint
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = currentPosition.coerceAtMost(duration).toFloat(),
                onValueChange = { player.seekTo(it.toLong()) },
                valueRange = 0f..(duration.takeIf { it > 0 }?.toFloat() ?: 1f),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = WhiteText,          // Circular thumb color
                    activeTrackColor = WhiteText,    // Progress color
                    inactiveTrackColor = WhiteHint,  // Background track color
                ),
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        colors = SliderDefaults.colors(thumbColor = WhiteText),
                    )
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDuration(currentPosition),
                    color = WhiteHint
                )
                Text(
                    formatDuration(duration),
                    color = WhiteHint
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* prev song */ }) {
                    Icon(
                        painter = painterResource(R.drawable.skip_prev),
                        contentDescription = "Previous",
                        tint = WhiteText,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) player.play() else player.pause()
                    },
                    modifier = Modifier.size(64.dp)
                ) {
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

                IconButton(onClick = { /* next song */ }) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = "Next",
                        tint = WhiteText,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

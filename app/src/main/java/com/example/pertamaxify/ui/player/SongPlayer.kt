package com.example.pertamaxify.ui.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.HomeViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.RedBackground
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image as ComposeImage

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun MusicPlayerScreen(
    song: Song,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    email: String? = null,
    homeViewModel: HomeViewModel = hiltViewModel(),
    isSongFromServer: Boolean = false,
    serverId: Int? = -1
) {
    val context = LocalContext.current

    var isLiked by remember { mutableStateOf(song.isLiked ?: false) }
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

    var showDeviceDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(player) {
        while (true) {
            currentPosition = player.currentPosition
            duration = player.duration.takeIf { it > 0 } ?: duration
            delay(500L)
        }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                    val temp = selectedDevice
                    selectedDevice = null
                    Toast.makeText(
                        context,
                        "Output Device $temp disconected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        context.registerReceiver(receiver, filter)

        onDispose {
            player.release()
            context.unregisterReceiver(receiver)
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RedBackground)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSongFromServer) {
                        IconButton(onClick = {
                            val shareLink = "purrytify://song/${serverId}"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, shareLink)
                                type = "text/plain"
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent, "Share song via"
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = WhiteText,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    IconButton(onClick = { showDeviceDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.devices),
                            contentDescription = "Device output",
                            tint = WhiteText,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = WhiteText
                    )
                }
            }

            if (!isSongFromServer) {
                IconButton(onClick = {
                    isLiked = !isLiked
                    val updatedSong = song.copy(isLiked = isLiked)
                    homeViewModel.updateSong(updatedSong)
                }) {
                    Icon(
                        painter = painterResource(
                            if (isLiked) R.drawable.tabler_heart_filled else R.drawable.tabler_heart
                        ),
                        contentDescription = if (isLiked) "Unlike" else "Like",
                        tint = if (isLiked) Color(0xFFFF80AB) else WhiteText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Artwork
            if (song.artwork.isNullOrBlank()) {
                ComposeImage(
                    painter = painterResource(id = R.drawable.song_image_placeholder),
                    contentDescription = "Album artwork",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            } else {
                AsyncImage(
                    model = song.artwork,
                    contentDescription = "Album artwork",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    error = painterResource(id = R.drawable.song_image_placeholder)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                song.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = WhiteText
            )
            Text(
                song.artist,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = WhiteHint
            )

            selectedDevice?.let {
                Text("Output: $it", color = WhiteHint, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = currentPosition.coerceAtMost(duration).toFloat(),
                onValueChange = { player.seekTo(it.toLong()) },
                valueRange = 0f..(duration.takeIf { it > 0 }?.toFloat() ?: 1f),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = WhiteText,
                    activeTrackColor = WhiteText,
                    inactiveTrackColor = WhiteHint
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(currentPosition), color = WhiteHint)
                Text(formatDuration(duration), color = WhiteHint)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous */ }) {
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
                    }, modifier = Modifier.size(64.dp)
                ) {
                    if (isPlaying) {
                        Icon(
                            painter = painterResource(R.drawable.pause),
                            contentDescription = "Pause",
                            tint = WhiteText,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = WhiteText,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                IconButton(onClick = { /* Next */ }) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = "Next",
                        tint = WhiteText,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        if (showDeviceDialog) {
            DeviceSelectionDialog(onDismiss = { showDeviceDialog = false }, onDeviceSelected = {
                selectedDevice = it.toString()
                showDeviceDialog = false
            })
        }
    }
}

@Composable
fun DeviceSelectionDialog(
    onDismiss: () -> Unit, onDeviceSelected: (AudioDeviceInfo) -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    data class DeviceEntry(val info: AudioDeviceInfo, val label: String)

    val deviceEntries = remember { mutableStateListOf<DeviceEntry>() }
    var selectedDevice by remember { mutableStateOf<AudioDeviceInfo?>(null) }

    LaunchedEffect(Unit) {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        devices.forEach { d ->
            Log.d(
                "AudioDevice", "Detected  ${d.productName}  | id=${d.id}  | type=${d.type}"
            )
        }

        val nameBuckets: MutableMap<String, MutableList<AudioDeviceInfo>> = mutableMapOf()
        devices.forEach { d ->
            val key = d.productName?.toString() ?: "Unknown Device"
            nameBuckets.getOrPut(key) { mutableListOf() }.add(d)
        }

        val entries = mutableListOf<DeviceEntry>()
        nameBuckets.forEach { (name, list) ->
            if (list.size == 1) {
                val info = list.first()
                val finalLabel =
                    if (info.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) "This Device"
                    else name
                entries += DeviceEntry(info, finalLabel)
            } else {
                list.forEachIndexed { idx, info ->
                    val numbered = "$name #${idx + 1}"
                    entries += DeviceEntry(info, numbered)
                }
            }
        }

        deviceEntries.clear()
        deviceEntries.addAll(entries)

        selectedDevice =
            entries.firstOrNull { it.info.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }?.info
                ?: entries.firstOrNull()?.info
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                selectedDevice?.let(onDeviceSelected)
                onDismiss()
            }) { Text("Choose", color = WhiteText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = WhiteHint) }
        },
        title = { Text("Output Device", color = WhiteText) },
        containerColor = RedBackground,
        textContentColor = WhiteText,
        text = {
            Column {
                deviceEntries.forEach { entry ->
                    val isChecked = selectedDevice?.id == entry.info.id
                    val statusText = if (isChecked) "(Connected)" else "(Disconnected)"
                    val statusColor = if (isChecked) Color.Green else Color.Gray

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDevice = entry.info }
                            .padding(vertical = 6.dp)) {
                        RadioButton(
                            selected = isChecked,
                            onClick = { selectedDevice = entry.info },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = WhiteText, unselectedColor = WhiteHint
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(entry.label, color = WhiteText)
                            Text(
                                statusText,
                                color = statusColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        })
}

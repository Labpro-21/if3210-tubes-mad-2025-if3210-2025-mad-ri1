package com.example.pertamaxify.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.HomeViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.song.RecentlyPlayedSection
import com.example.pertamaxify.utils.JwtUtils
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.ui.song.NewSongsSection
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    selectedSong: Song?,
    onSongSelected: (Song) -> Unit
) {
    val context = LocalContext.current

    // For deletion confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    // Get user email from token
    val accessToken = SecurePrefs.getAccessToken(context)
    val email = remember {
        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val username = jwtPayload?.username ?: ""
            if (username.isNotEmpty()) "$username@std.stei.itb.ac.id" else ""
        } else {
            ""
        }
    }

    val recentlyPlayedSongs by viewModel.recentlyPlayedSongs.collectAsState()
    val recentlyAddedSongs by viewModel.recentlyAddedSongs.collectAsState()

    // This effect will run whenever the HomeScreen is displayed
    LaunchedEffect(Unit) {
        // Refresh data when screen is shown
        viewModel.refreshAllData()
    }

    // Delete confirmation dialog
    if (showDeleteDialog && songToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                songToDelete = null
            },
            title = { Text("Delete Song") },
            text = { Text("Are you sure you want to delete ${songToDelete?.title}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        songToDelete?.let { song ->
                            viewModel.deleteSong(song)
                        }
                        showDeleteDialog = false
                        songToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        songToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp, 24.dp)
    ) {
        NewSongsSection(
            songs = recentlyAddedSongs,
            onSongClick = { song ->
                // Update recently played timestamp when song is clicked
                viewModel.updateSongPlayedTimestamp(song, email)
                // Notify parent that song was selected
                onSongSelected(song)
            },
            onToggleLike = { song ->
                viewModel.toggleLikeSong(song)
            },
            onDeleteSong = { song ->
                // Show confirmation dialog
                songToDelete = song
                showDeleteDialog = true
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        RecentlyPlayedSection(
            songs = recentlyPlayedSongs,
            onSongClick = { song ->
                // Update recently played timestamp when song is clicked
                viewModel.updateSongPlayedTimestamp(song, email)
                // Notify parent that song was selected
                onSongSelected(song)
            },
            onToggleLike = { song ->
                viewModel.toggleLikeSong(song)
            },
            onDeleteSong = { song ->
                // Show confirmation dialog
                songToDelete = song
                showDeleteDialog = true
            }
        )
    }
}
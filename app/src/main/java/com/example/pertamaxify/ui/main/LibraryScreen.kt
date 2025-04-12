package com.example.pertamaxify.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.ProfileResponse
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.library.AddSongDialog
import com.example.pertamaxify.ui.song.SongListRecyclerView
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: LibraryViewModel = hiltViewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val profile by remember { mutableStateOf<ProfileResponse?>(null) }

    // Observe StateFlows from the VM
    val allSongs by viewModel.allSongs.collectAsState()
    val likedSongs by viewModel.likedSongs.collectAsState()

    // Keep track of which tab is active
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header row with “Your Library” and a plus icon to add a song
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Your Library",
                modifier = Modifier.padding(16.dp),
                color = WhiteText,
                style = Typography.titleLarge
            )
            Text(
                "+",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDialog = true },
                color = WhiteText,
                style = Typography.titleLarge
            )
        }

        // The Dialog to add a new song
        if (showDialog) {
            AddSongDialog(
                onDismiss = { showDialog = false },
                onSave = { title, artist, imagePath, audioPath, email ->
                    viewModel.saveSong(
                        Song(
                            title = title,
                            singer = artist,
                            imagePath = imagePath,
                            audioPath = audioPath,
                            addedBy = email
                        )
                    )
                    showDialog = false
                }
            )
        }

        // Create the Tabs for “All” and “Liked”
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = (selectedTabIndex == 0),
                onClick = { selectedTabIndex = 0 },
                text = { Text("All") }
            )
            Tab(
                selected = (selectedTabIndex == 1),
                onClick = { selectedTabIndex = 1 },
                text = { Text("Liked") }
            )
        }

        // Based on the selected tab, show the corresponding RecyclerView
        when (selectedTabIndex) {
            0 -> {
                // All Songs
                SongListRecyclerView(
                    songs = allSongs,
                    onToggleLike = { song -> viewModel.toggleLike(song) }
                )
            }
            1 -> {
                // Liked Songs
                SongListRecyclerView(
                    songs = likedSongs,
                    onToggleLike = { song -> viewModel.toggleLike(song) }
                )
            }
        }
    }
}

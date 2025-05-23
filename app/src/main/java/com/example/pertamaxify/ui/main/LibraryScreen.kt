package com.example.pertamaxify.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.MainViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.library.AddSongDialog
import com.example.pertamaxify.ui.song.SongListRecyclerView
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText
import com.example.pertamaxify.utils.JwtUtils

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val accessToken = SecurePrefs.getAccessToken(context)
    val username: String?
    val email: String?
    if (!accessToken.isNullOrEmpty()) {
        val jwtPayload = JwtUtils.decodeJwt(accessToken)
        username = jwtPayload?.username ?: ""

        email = "$username@std.stei.itb.ac.id"
    } else {
        email = ""
    }

    // Observe StateFlows from the VM
    val allSongs by viewModel.allSongs.collectAsState()
    val likedSongs by viewModel.likedSongs.collectAsState()
    val downloadedSongs by viewModel.downloadedSongs.collectAsState()

    // Keep track of which tab is active
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            viewModel.refreshAllData(email)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header row with "Your Library" and a plus icon to add a song
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
                    if (email != null) {
                        viewModel.saveSong(
                            Song(
                                title = title,
                                artist = artist,
                                artwork = imagePath,
                                url = audioPath,
                                addedBy = email
                            ),
                            email = email
                        )
                    }
                    showDialog = false
                },
                email = email
            )
        }

        // Create the Tabs for "All" and "Liked"
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
            Tab(
                selected = (selectedTabIndex == 2),
                onClick = { selectedTabIndex = 2 },
                text = { Text("Downloaded") }
            )
        }

        // Based on the selected tab, show the corresponding RecyclerView
        when (selectedTabIndex) {
            0 -> {
                // All Songs
                SongListRecyclerView(
                    songs = allSongs,
                    onSongClick = { song ->
                        // Play the song using MainViewModel
                        mainViewModel.updateSelectedSong(song)
                    },
                    onSongLongClick = { song ->
                        // Handle like/unlike or other actions
//                        viewModel.toggleLike(song)
                    }
                )
            }
            1 -> {
                // Liked Songs
                SongListRecyclerView(
                    songs = likedSongs,
                    onSongClick = { song ->
                        mainViewModel.updateSelectedSong(song)
                    },
                    onSongLongClick = { song ->
//                        viewModel.toggleLike(song)
                    }
                )
            }
            2 -> {
                // Downloaded Songs
                SongListRecyclerView(
                    songs = downloadedSongs,
                    onSongClick = { song ->
                        mainViewModel.updateSelectedSong(song)
                    },
                    onSongLongClick = { song ->
//                        viewModel.toggleLike(song)
                    }
                )
            }
        }
    }
}
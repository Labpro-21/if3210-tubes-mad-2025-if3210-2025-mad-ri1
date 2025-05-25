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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
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

    val allSongs by viewModel.allSongs.collectAsState()
    val likedSongs by viewModel.likedSongs.collectAsState()
    val downloadedSongs by viewModel.downloadedSongs.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }


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

        TabRow(selectedTabIndex = selectedTabIndex) {
            if (showContextMenu && selectedSong != null) {
                AlertDialog(
                    onDismissRequest = { showContextMenu = false },
                    title = { Text("Options for '${selectedSong!!.title}'") },
                    text = {
                        Column {
                            val isLiked = selectedSong!!.isLiked == true
                            Text(
                                if (isLiked) "Remove from Liked" else "Add to Liked",
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        try {
                                            viewModel.toggleLike(selectedSong!!, email)
                                            showContextMenu = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = if (isLiked) "Removed from liked" else "Added to liked"
                                                )
                                            viewModel.refreshAllData(email)
                                            }
                                        } catch (e: Exception) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Failed to toggle like")
                                            }
                                        }
                                    }
                            )

                            Text(
                                "Delete Song",
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        try {
                                            viewModel.deleteSong(selectedSong!!, email)
                                            showContextMenu = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Song deleted")
                                            }
                                            viewModel.refreshAllData(email)
                                        } catch (e: Exception) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Failed to delete song")
                                            }
                                        }
                                    }
                            )

                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showContextMenu = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

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

        when (selectedTabIndex) {
            0 -> {
                // All Songs
                SongListRecyclerView(
                    songs = allSongs,
                    onSongClick = { song ->
                        mainViewModel.updateSelectedSong(song, email)
                    },
                    onSongLongClick = { song ->
                        selectedSong = song
                        showContextMenu = true
                    }
                )
            }
            1 -> {
                // Liked Songs
                SongListRecyclerView(
                    songs = likedSongs,
                    onSongClick = { song ->
                        mainViewModel.updateSelectedSong(song, email)
                    },
                    onSongLongClick = { song ->
                        selectedSong = song
                        showContextMenu = true
                    }
                )
            }
            2 -> {
                // Downloaded Songs
                SongListRecyclerView(
                    songs = downloadedSongs,
                    onSongClick = { song ->
                        mainViewModel.updateSelectedSong(song, email)
                    },
                    onSongLongClick = { song ->
                        selectedSong = song
                        showContextMenu = true
                    }
                )
            }
        }
    }
}
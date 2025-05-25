package com.example.pertamaxify.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.MainViewModel
import com.example.pertamaxify.data.model.Song
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

    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
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
                            ), email = email
                        )
                    }
                    showDialog = false
                },
                email = email
            )
        }

        SpotifyStyleTabs(
            tabTitles = listOf("All", "Liked", "Downloaded"),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it })

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
                                })

                        Text(
                            "Delete Song", modifier = Modifier
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
                                })

                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showContextMenu = false }) {
                        Text("Cancel")
                    }
                })
        }

        when (selectedTabIndex) {
            0 -> {
                // All Songs
                SongListRecyclerView(songs = allSongs, onSongClick = { song ->
                    mainViewModel.updateSelectedSong(song, email)
                }, onSongLongClick = { song ->
                    selectedSong = song
                    showContextMenu = true
                })
            }

            1 -> {
                // Liked Songs
                SongListRecyclerView(songs = likedSongs, onSongClick = { song ->
                    mainViewModel.updateSelectedSong(song, email)
                }, onSongLongClick = { song ->
                    selectedSong = song
                    showContextMenu = true
                })
            }

            2 -> {
                // Downloaded Songs
                SongListRecyclerView(songs = downloadedSongs, onSongClick = { song ->
                    mainViewModel.updateSelectedSong(song, email)
                }, onSongLongClick = { song ->
                    selectedSong = song
                    showContextMenu = true
                })
            }
        }
    }
}

@Composable
fun SpotifyStyleTabs(
    tabTitles: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val spotifyGreen = Color(0xFF1DB954)
    val grayBackground = Color(0xFF2A2A2A)
    val dividerGray = Color(0xFF444444)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = index == selectedTabIndex
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            color = if (isSelected) spotifyGreen else grayBackground,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onTabSelected(index) }) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Color.LightGray,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp, color = dividerGray
        )
    }
}
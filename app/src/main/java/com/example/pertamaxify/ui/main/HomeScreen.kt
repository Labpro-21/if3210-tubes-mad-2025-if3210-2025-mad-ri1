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
import com.example.pertamaxify.data.model.JwtPayload
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.song.RecentlyPlayedSection
import com.example.pertamaxify.utils.JwtUtils
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.ui.song.NewSongsSection

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    selectedSong: Song?,
    onSongSelected: (Song) -> Unit
) {
    val context = LocalContext.current

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
            }
        )
    }
}

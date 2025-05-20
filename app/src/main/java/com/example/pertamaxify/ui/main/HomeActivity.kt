package com.example.pertamaxify.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.HomeViewModel
import com.example.pertamaxify.data.model.MainViewModel
import com.example.pertamaxify.data.model.PlaylistViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.ui.player.MiniPlayer
import com.example.pertamaxify.ui.player.MusicPlayerScreen
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.utils.JwtUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var songRepository: SongRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                val mainViewModel: MainViewModel = hiltViewModel()
                val homeViewModel: HomeViewModel = hiltViewModel()
                val playlistViewModel: PlaylistViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    homeViewModel.refreshAllData()
                }

                MainScreen(
                    mainViewModel = mainViewModel,
                    homeViewModel = homeViewModel,
                    playlistViewModel = playlistViewModel,
                    songRepository = songRepository
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    playlistViewModel: PlaylistViewModel,
    songRepository: SongRepository
) {
    val context = LocalContext.current

    // Get user email from token
    val userEmail = remember {
        val accessToken = SecurePrefs.getAccessToken(context)
        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val username = jwtPayload?.username ?: ""
            if (username.isNotEmpty()) "$username@std.stei.itb.ac.id" else ""
        } else {
            ""
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    var selectedSong by mainViewModel.selectedSong
    var isPlayerVisible by mainViewModel.isPlayerVisible
    var isPlayingOnlineSong by remember { mutableStateOf(false) }
    var currentOnlineSong by remember { mutableStateOf<SongResponse?>(null) }

    // When tab selection changes to Home, refresh data
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            homeViewModel.refreshAllData()
            playlistViewModel.fetchGlobalTopSongs()
            playlistViewModel.fetchCountryTopSongs(playlistViewModel.selectedCountry.value)
        }
    }

    Scaffold(
        bottomBar = { NavBar(selectedTab, onTabSelected = { selectedTab = it }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    viewModel = homeViewModel,
                    playlistViewModel = playlistViewModel,
                    selectedSong = selectedSong,
                    onSongSelected = { newSong ->
                        // Playing a local song
                        isPlayingOnlineSong = false
                        currentOnlineSong = null
                        mainViewModel.updateSelectedSong(newSong)

                        // Also update the song's recently played time
                        homeViewModel.updateSongPlayedTimestamp(newSong, userEmail)
                    },
                    onOnlineSongSelected = { onlineSong ->
                        // Playing an online song
                        isPlayingOnlineSong = true
                        currentOnlineSong = onlineSong

                        // Not updating any database when playing online song
                        isPlayerVisible = true
                    }
                )
                1 -> LibraryScreen()
                2 -> ProfileScreen()
            }

            // Show player for local songs
            if (!isPlayingOnlineSong && selectedSong != null) {
                if (isPlayerVisible) {
                    // Show full Music Player
                    MusicPlayerScreen(
                        song = selectedSong!!,
                        onDismiss = { mainViewModel.dismissPlayer() },
                        modifier = Modifier.align(Alignment.Center),
                        email = userEmail,
                        songRepository = songRepository,
                        homeViewModel = homeViewModel
                    )
                } else {
                    // Show Mini Player
                    MiniPlayer(
                        song = selectedSong!!,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onClick = { isPlayerVisible = true }
                    )
                }
            }

            // Show player for online songs
            if (isPlayingOnlineSong && currentOnlineSong != null) {
                // Create a temporary Song object from the online song
                val tempSong = Song(
                    id = 0,
                    title = currentOnlineSong!!.title,
                    artist = currentOnlineSong!!.artist,
                    artwork = currentOnlineSong!!.artwork,
                    url = currentOnlineSong!!.url,
                    duration = currentOnlineSong!!.convertDurationToSeconds(currentOnlineSong!!.duration),
                    isDownloaded = false,
                    addedTime = Date()
                )

                if (isPlayerVisible) {
                    // Show full Music Player for online song
                    MusicPlayerScreen(
                        song = tempSong,
                        onDismiss = {
                            isPlayerVisible = false
                            // Don't save anything to database since it's an online song
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    // Show Mini Player for online song
                    MiniPlayer(
                        song = tempSong,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onClick = { isPlayerVisible = true }
                    )
                }
            }
        }
    }
}

// Extension function to convert duration string (mm:ss) to seconds
private fun SongResponse.convertDurationToSeconds(duration: String): Int {
    try {
        val parts = duration.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()
            return minutes * 60 + seconds
        }
    } catch (e: Exception) {
        // Return 0 if parsing fails
    }
    return 0
}
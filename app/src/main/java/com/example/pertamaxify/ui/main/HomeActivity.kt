package com.example.pertamaxify.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.HomeViewModel
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.MainViewModel
import com.example.pertamaxify.data.model.ProfileViewModel
import com.example.pertamaxify.data.model.PlaylistViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.data.model.StatisticViewModel
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.player.MusicPlayerManager
import com.example.pertamaxify.ui.library.LibraryScreen
import com.example.pertamaxify.ui.player.MiniPlayer
import com.example.pertamaxify.ui.player.MusicPlayerScreen
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.utils.JwtUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var musicPlayerManager: MusicPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deepLinkId = intent.getIntExtra("DEEP_LINK_SERVER_ID", -1)
        if (deepLinkId != -1) {
            Log.d("Home Activity", "deeplink $deepLinkId")
        }

        setContent {
            PertamaxifyTheme {
                val mainViewModel: MainViewModel = hiltViewModel()
                val homeViewModel: HomeViewModel = hiltViewModel()
                val playlistViewModel: PlaylistViewModel = hiltViewModel()
                val libraryViewModel: LibraryViewModel = hiltViewModel()
                val statisticViewModel: StatisticViewModel = hiltViewModel()
                val profileViewModel: ProfileViewModel = hiltViewModel()

                MainScreen(
                    mainViewModel = mainViewModel,
                    homeViewModel = homeViewModel,
                    playlistViewModel = playlistViewModel,
                    libraryViewModel = libraryViewModel,
                    statisticViewModel = statisticViewModel,
                    profileViewModel = profileViewModel,
                    musicPlayerManager = musicPlayerManager,
                    deepLinkServerId = if (deepLinkId != -1) deepLinkId else null
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
    libraryViewModel: LibraryViewModel,
    statisticViewModel: StatisticViewModel,
    musicPlayerManager: MusicPlayerManager,
    profileViewModel: ProfileViewModel,
    deepLinkServerId: Int? = null
) {
    val context = LocalContext.current
    val accessToken = SecurePrefs.getAccessToken(context)
    val username: String?
    val userEmail: String?
    if (!accessToken.isNullOrEmpty()) {
        val jwtPayload = JwtUtils.decodeJwt(accessToken)
        username = jwtPayload?.username ?: ""
        userEmail = "$username@std.stei.itb.ac.id"
    } else {
        userEmail = ""
    }

    var selectedTab by remember { mutableStateOf(0) }
    var selectedSong by mainViewModel.selectedSong
    var isPlayerVisible by mainViewModel.isPlayerVisible
    var isPlayingOnlineSong by remember { mutableStateOf(false) }
    var currentOnlineSong by remember { mutableStateOf<SongResponse?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle back press when player is visible
    BackHandler(enabled = isPlayerVisible) {
        mainViewModel.dismissPlayer()
    }

    LaunchedEffect(deepLinkServerId) {
        deepLinkServerId?.let { id ->
            val song = playlistViewModel.getSongByServerId(id)
            if (song != null) {
                isPlayingOnlineSong = true
                currentOnlineSong = song
                isPlayerVisible = true
            }
        }
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            homeViewModel.refreshAllData(userEmail)
        }
    }

    // When tab selection changes, refresh data
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> {
                homeViewModel.refreshAllData(userEmail)
                playlistViewModel.fetchGlobalTopSongs()
                playlistViewModel.fetchCountryTopSongs(playlistViewModel.selectedCountry.value)
            }

            1 -> {
                libraryViewModel.refreshAllData(userEmail)
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = { NavBar(selectedTab, onTabSelected = { selectedTab = it }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            ,
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

                        mainViewModel.updateSelectedSong(newSong, userEmail)
                        musicPlayerManager.playSong(newSong, false, -1)
                    },
                    onOnlineSongSelected = { onlineSong ->
                        // Playing an online song
                        isPlayingOnlineSong = true
                        currentOnlineSong = onlineSong

                        // Create a temporary Song object from the online song
                        val tempSong = Song(
                            id = 0,
                            title = onlineSong.title,
                            artist = onlineSong.artist,
                            artwork = onlineSong.artwork,
                            url = onlineSong.url,
                            duration = onlineSong.convertDurationToSeconds(onlineSong.duration),
                            isDownloaded = false,
                            addedTime = Date()
                        )

                        musicPlayerManager.playSong(tempSong, true, onlineSong.id)
                        isPlayerVisible = true
                    }
                )

                1 -> LibraryScreen(
                    viewModel = libraryViewModel,
                    mainViewModel = mainViewModel,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )

                2 -> ProfileScreen(
                    statisticViewModel = statisticViewModel,
                    profileViewModel = profileViewModel
                )

                3 -> QRScannerScreen { serverId ->
                    playlistViewModel.viewModelScope.launch {
                        val songResp = playlistViewModel.getSongByServerId(serverId)
                        songResp?.let {
                            isPlayingOnlineSong = true
                            currentOnlineSong = it

                            // Create a temporary Song object from the online song
                            val tempSong = Song(
                                id = 0,
                                title = it.title,
                                artist = it.artist,
                                artwork = it.artwork,
                                url = it.url,
                                duration = it.convertDurationToSeconds(it.duration),
                                isDownloaded = false,
                                addedTime = Date()
                            )

                            musicPlayerManager.playSong(tempSong, true, it.id)
                            isPlayerVisible = true
                            selectedTab = 0
                        }
                    }
                }
            }

            // Show player for local songs
            if (!isPlayingOnlineSong && selectedSong != null) {
                if (isPlayerVisible) {
                    // Show full Music Player
                    MusicPlayerScreen(
                        song = selectedSong!!,
                        onDismiss = { mainViewModel.dismissPlayer() },
                        musicPlayerManager = musicPlayerManager,
                        modifier = Modifier.align(Alignment.Center),
                        email = userEmail,
                        homeViewModel = homeViewModel,
                        isSongFromServer = false,
                        serverId = -1
                    )
                } else {
                    // Show Mini Player
                    MiniPlayer(
                        song = selectedSong!!,
                        musicPlayerManager = musicPlayerManager,
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
                        },
                        musicPlayerManager = musicPlayerManager,
                        modifier = Modifier.align(Alignment.Center),
                        isSongFromServer = true,
                        serverId = currentOnlineSong!!.id
                    )
                } else {
                    // Show Mini Player for online song
                    MiniPlayer(
                        song = tempSong,
                        musicPlayerManager = musicPlayerManager,
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
        Log.e("SongResponse", "Error converting duration to seconds: ${e.message}")
    }
    return 0
}
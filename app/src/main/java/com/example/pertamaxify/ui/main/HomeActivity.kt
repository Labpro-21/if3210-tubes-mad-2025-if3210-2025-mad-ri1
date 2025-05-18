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
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.ui.player.MiniPlayer
import com.example.pertamaxify.ui.player.MusicPlayerScreen
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.utils.JwtUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var songRepository: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val homeViewModel: HomeViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    viewModel.loadLastPlayedSong()
                    homeViewModel.refreshAllData()
                }

                MainScreen(
                    mainViewModel = viewModel,
                    homeViewModel = homeViewModel,
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

    // When tab selection changes to Home, refresh data
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            homeViewModel.refreshAllData()
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
                    selectedSong = selectedSong,
                    onSongSelected = { newSong ->
                        mainViewModel.updateSelectedSong(newSong)

                        // Also update the song's recently played time
                        homeViewModel.updateSongPlayedTimestamp(newSong, userEmail)
                    }
                )
                1 -> LibraryScreen()
                2 -> ProfileScreen()
            }

            if (selectedSong != null) {
                if (isPlayerVisible) {
                    // Show full Music Player
                    MusicPlayerScreen(
                        song = selectedSong!!,
                        onDismiss = { mainViewModel.dismissPlayer() },
                        modifier = Modifier.align(Alignment.Center),
                        email = userEmail,
                        songRepository = songRepository
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
        }
    }
}

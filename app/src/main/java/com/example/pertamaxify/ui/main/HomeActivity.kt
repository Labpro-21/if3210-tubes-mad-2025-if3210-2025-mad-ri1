package com.example.pertamaxify.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.data.model.MainViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.player.MusicPlayerScreen
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.example.pertamaxify.data.model.ProfileResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                val viewModel: MainViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    viewModel.loadLastPlayedSong()
                }
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val selectedSong by viewModel.selectedSong
    val isPlayerVisible by viewModel.isPlayerVisible
    selectedSong?.let { Log.d("Init selected song: ", it.title + " - " + it.singer) }

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
                    selectedSong = selectedSong,
                    onSongSelected = { newSong ->
                        viewModel.updateSelectedSong(newSong)
                    }
                )
                1 -> LibraryScreen()
                2 -> ProfileScreen()
            }

            // Player overlay (appears when song changes)
            if (isPlayerVisible && selectedSong != null) {
                MusicPlayerScreen(
                    song = selectedSong!!,
                    onDismiss = { viewModel.dismissPlayer() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

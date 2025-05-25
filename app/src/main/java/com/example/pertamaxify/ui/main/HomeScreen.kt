package com.example.pertamaxify.ui.main

import android.util.Log
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
import com.example.pertamaxify.data.model.PlaylistViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.ui.song.CountryTopSection
import com.example.pertamaxify.ui.song.GlobalTopSection
import com.example.pertamaxify.ui.song.NewSongsSection
import com.example.pertamaxify.ui.song.RecentlyPlayedSection
import com.example.pertamaxify.utils.JwtUtils
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.ui.song.NewSongsSection
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.pertamaxify.services.DownloaderService

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    selectedSong: Song?,
    onSongSelected: (Song) -> Unit,
    onOnlineSongSelected: (SongResponse) -> Unit
) {
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

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

    val globalTopSongs by playlistViewModel.globalTopSongs.collectAsState()
    val countryTopSongs by playlistViewModel.countryTopSongs.collectAsState()
    val selectedCountry by playlistViewModel.selectedCountry
    val isLoadingGlobal by playlistViewModel.isLoadingGlobal
    val isLoadingCountry by playlistViewModel.isLoadingCountry
    val globalErrorMessage by playlistViewModel.globalErrorMessage
    val countryErrorMessage by playlistViewModel.countryErrorMessage

    LaunchedEffect(Unit) {
        viewModel.refreshAllData(email = email)
        playlistViewModel.fetchGlobalTopSongs()
        playlistViewModel.fetchCountryTopSongs(selectedCountry)
    }

    val handleDownload = { song: SongResponse ->
        Log.d("HomeScreen", "Starting download for: ${song.title}")
        DownloaderService.startDownload(context, song, email)
    }

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
                            viewModel.deleteSong(song, email)
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
        // Global Top 50 Section
        GlobalTopSection(
            songs = globalTopSongs,
            isLoading = isLoadingGlobal,
            errorMessage = globalErrorMessage,
            onSongClick = { song ->
                onOnlineSongSelected(song)
            },
            onDownload = handleDownload,
            email = email
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Country Top 10 Section
        CountryTopSection(
            songs = countryTopSongs,
            isLoading = isLoadingCountry,
            errorMessage = countryErrorMessage,
            onSongClick = { song ->
                onOnlineSongSelected(song)
            },
            selectedCountry = selectedCountry,
            countryName = playlistViewModel.getCountryName(selectedCountry),
            supportedCountries = playlistViewModel.getSupportedCountries(),
            getCountryName = { playlistViewModel.getCountryName(it) },
            onCountrySelected = { countryCode ->
                playlistViewModel.fetchCountryTopSongs(countryCode)
            },
            onDownload = handleDownload,
            email = email
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (recentlyAddedSongs.isNotEmpty()) {
            NewSongsSection(
                songs = recentlyAddedSongs,
                onSongClick = { song ->
                    onSongSelected(song)
                },
                onToggleLike = { song ->
                    viewModel.toggleLikeSong(song)
                },
                onDeleteSong = { song ->
                    songToDelete = song
                    showDeleteDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (recentlyPlayedSongs.isNotEmpty()) {
            RecentlyPlayedSection(
                songs = recentlyPlayedSongs,
                onSongClick = { song ->
                    onSongSelected(song)
                },
                onToggleLike = { song ->
                    viewModel.toggleLikeSong(song)
                },
                onDeleteSong = { song ->
                    songToDelete = song
                    showDeleteDialog = true
                }
            )
        }
    }
}
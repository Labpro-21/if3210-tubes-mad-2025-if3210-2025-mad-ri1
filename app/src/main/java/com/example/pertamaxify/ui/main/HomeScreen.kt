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

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val token = remember { mutableStateOf("") }
    val decodedPayload = remember { mutableStateOf<JwtPayload?>(null) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }

    // Dummy data
    val newSongs = remember {
        listOf(
            Song("Starboy", "The Weeknd, Daft Punk", "content://media/external/file/1000000033", "content://media/external/file/1000000035"),
            Song("Here Comes The Sun", "The Beatles", "content://media/external/file/1000000034", "..."),
            Song("Midnight Pretenders", "Tomoko Aran", "Sickboy Chainsmoker.png", "..."),
            Song("Violent Crimes", "Kanye West", "Sickboy Chainsmoker.png", "...")
        )
    }

    LaunchedEffect(Unit) {
        token.value = SecurePrefs.getAccessToken(context) ?: "No token found"
        decodedPayload.value = JwtUtils.decodeJwt(token.value)
    }


//    // Show player when a song is clicked
//    selectedSong?.let { song ->
//        MusicPlayerScreen(song = song)
//        return
//    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().verticalScroll(
        rememberScrollState()
    ).padding(16.dp, 24.dp)) {
//        NewSongsSection(songs = newSongs, onSongClick = { song ->
//            selectedSong = song
//        })
        Spacer(modifier = Modifier.height(24.dp))
        RecentlyPlayedSection(songs = viewModel.recentlyPlayedSongs)
    }
}

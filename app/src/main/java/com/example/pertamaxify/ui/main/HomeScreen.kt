package com.example.pertamaxify.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.JwtPayload
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.player.MusicPlayerScreen
import com.example.pertamaxify.ui.song.NewSongsSection
import com.example.pertamaxify.ui.song.RecentlyPlayedSection
import com.example.pertamaxify.ui.theme.WhiteText
import com.example.pertamaxify.utils.JwtUtils

@Composable
fun HomeScreen() {
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

    val recentlyPlayed = remember {
        listOf(
            Song("Jazz is for ordinary people", "berlioz", "Sickboy Chainsmoker.png", "..."),
            Song("Loose", "Daniel Caesar", "Sickboy Chainsmoker.png", "..."),
            Song("Nights", "Frank Ocean", "Sickboy Chainsmoker.png", "..."),
            Song("Kiss of Life", "Sade", "Sickboy Chainsmoker.png", "..."),
            Song("BEST INTEREST", "Tyler, The Creator", "Sickboy Chainsmoker.png", "...")
        )
    }

    LaunchedEffect(Unit) {
        token.value = SecurePrefs.getAccessToken(context) ?: "No token found"
        decodedPayload.value = JwtUtils.decodeJwt(token.value)
    }


    // If a song is selected, show the music player screen
    selectedSong?.let { song ->
        MusicPlayerScreen(song = song) {
            selectedSong = null // go back when back button pressed
        }
        return // Skip the rest of HomeScreen UI
    }

    // If a song is selected, show the music player screen
    selectedSong?.let { song ->
        MusicPlayerScreen(song = song) {
            selectedSong = null // go back when back button pressed
        }
        return // Skip the rest of HomeScreen UI
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().verticalScroll(
        rememberScrollState()
    )) {
        Text(text = "Home Page", style = MaterialTheme.typography.headlineMedium, color = WhiteText)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Token: ${token.value}", style = MaterialTheme.typography.bodyLarge, color = WhiteText)
        Spacer(modifier = Modifier.height(16.dp))

        decodedPayload.value?.let {
            Text(text = "Decoded JWT:", style = MaterialTheme.typography.headlineSmall, color = WhiteText)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ID: ${it.id}", style = MaterialTheme.typography.bodyLarge, color = WhiteText)
            Text(text = "Username: ${it.username}", style = MaterialTheme.typography.bodyLarge, color = WhiteText)
            Text(text = "Issued At: ${it.iat}", style = MaterialTheme.typography.bodyLarge, color = WhiteText)
            Text(text = "Expires At: ${it.exp}", style = MaterialTheme.typography.bodyLarge, color = WhiteText)
        } ?: Text(text = "Failed to decode JWT", style = MaterialTheme.typography.bodyLarge, color = WhiteText)

        Spacer(modifier = Modifier.height(24.dp))

        NewSongsSection(songs = newSongs, onSongClick = { song ->
            selectedSong = song
        })
        Spacer(modifier = Modifier.height(24.dp))
        RecentlyPlayedSection(songs = recentlyPlayed)
    }
}

package com.example.pertamaxify.ui.song

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.ui.theme.WhiteText

@Composable
fun GlobalTopSection(
    songs: List<SongResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onSongClick: (SongResponse) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp)) {
        Text(
            "Top 50 Global",
            style = MaterialTheme.typography.titleLarge,
            color = WhiteText,
            modifier = Modifier.padding(0.dp, 4.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            songs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs found in global playlist",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyRow {
                    itemsIndexed(songs) { index, song ->
                        OnlineSongItem(
                            song = song,
                            onSongClick = onSongClick,
                            type = "horizontal",
                            rank = index + 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CountryTopSection(
    songs: List<SongResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onSongClick: (SongResponse) -> Unit,
    selectedCountry: String,
    countryName: String,
    supportedCountries: List<String>,
    getCountryName: (String) -> String,
    onCountrySelected: (String) -> Unit
) {
    var showCountryMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp)) {
        // Section title with country selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp)
        ) {
            Text(
                "Top 10 $countryName",
                style = MaterialTheme.typography.titleLarge,
                color = WhiteText,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            OutlinedButton(
                onClick = { showCountryMenu = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("Change Country")
            }

            // Country selection dropdown
            DropdownMenu(
                expanded = showCountryMenu,
                onDismissRequest = { showCountryMenu = false }
            ) {
                supportedCountries.forEach { countryCode ->
                    DropdownMenuItem(
                        text = {
                            Text(getCountryName(countryCode))
                        },
                        onClick = {
                            onCountrySelected(countryCode)
                            showCountryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            songs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs found for $countryName",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                // Show songs as a list
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    itemsIndexed(songs) { index, song ->
                        OnlineSongItem(
                            song = song,
                            onSongClick = onSongClick,
                            type = "vertical",
                            rank = index + 1
                        )
                    }
                }
            }
        }
    }
}
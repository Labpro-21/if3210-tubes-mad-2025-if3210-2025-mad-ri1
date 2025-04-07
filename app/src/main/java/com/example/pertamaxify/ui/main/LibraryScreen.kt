package com.example.pertamaxify.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pertamaxify.ui.theme.WhiteText

@Composable
fun LibraryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Your Library", style = MaterialTheme.typography.headlineMedium, color = WhiteText)
    }
}

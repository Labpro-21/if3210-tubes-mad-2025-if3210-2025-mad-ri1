package com.example.pertamaxify.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.JwtPayload
import com.example.pertamaxify.ui.theme.WhiteText
import com.example.pertamaxify.utils.JwtUtils

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val token = remember { mutableStateOf("") }
    val decodedPayload = remember { mutableStateOf<JwtPayload?>(null) }

    LaunchedEffect(Unit) {
        token.value = SecurePrefs.getAccessToken(context) ?: "No token found"
        decodedPayload.value = JwtUtils.decodeJwt(token.value)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
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
    }
}

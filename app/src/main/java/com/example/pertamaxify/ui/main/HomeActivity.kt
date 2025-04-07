package com.example.pertamaxify.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.JwtPayload
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.ui.theme.WhiteText
import com.example.pertamaxify.utils.JwtUtils

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                HomeScreen()
            }
        }
    }

    @Composable
    fun HomeScreen() {
        val token = remember { mutableStateOf("") }
        val decodedPayload = remember { mutableStateOf<JwtPayload?>(null) }

        // Load token and decode it
        LaunchedEffect(Unit) {
            token.value = SecurePrefs.getAccessToken(this@HomeActivity) ?: "No token found"
            decodedPayload.value = JwtUtils.decodeJwt(token.value)
        }

        // UI
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Home Page",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Token: ${token.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(16.dp))

                decodedPayload.value?.let {
                    Text(
                        text = "Decoded JWT:",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WhiteText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID: ${it.id}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WhiteText
                    )
                    Text(
                        text = "Username: ${it.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WhiteText
                    )
                    Text(
                        text = "Issued At: ${it.iat}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WhiteText
                    )
                    Text(
                        text = "Expires At: ${it.exp}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WhiteText
                    )
                } ?: Text(
                    text = "Failed to decode JWT",
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhiteText
                )
            }
        }
    }
}

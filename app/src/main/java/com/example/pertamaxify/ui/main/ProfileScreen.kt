package com.example.pertamaxify.ui.main

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.ProfileResponse
import com.example.pertamaxify.data.remote.ApiClient
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.network.NetworkUtils
import com.example.pertamaxify.ui.network.NoConnectionScreen

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val token = SecurePrefs.getAccessToken(context)
    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showNoConnection by remember { mutableStateOf(false) }

    val isConnected by NetworkUtils.isConnected.collectAsState()
    val client = remember { ApiClient.instance }

    LaunchedEffect(Unit) {
        NetworkUtils.registerNetworkCallback(context)
    }

    LaunchedEffect(isConnected) {
        if (profile == null && isConnected) {
            try {
                token?.let {
                    val res = client.getProfile("Bearer $it")
                    if (res.isSuccessful) {
                        profile = res.body()
                        showNoConnection = false
                    } else {
                        Log.e("ProfileScreen", "API Error: ${res.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Exception: ${e.localizedMessage}")
            }
        } else if (!isConnected && profile == null) {
            showNoConnection = true
        }
    }

    if (showNoConnection) {
        NoConnectionScreen()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00667B), Color(0xFF002F38), Color(0xFF101010)),
                    startY = 100f,
                    endY = 1400f
                )
            )
            .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "http://34.101.226.132:3000/uploads/profile-picture/${profile?.profilePhoto ?: "dummy.png"}"
                    ),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Icon",
                    tint = Color(0xFF007F99),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(6.dp)
                        .clickable { showDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile?.username ?: "Username",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Indonesia",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E3F3F))
            ) {
                Text("Edit Profile", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("123", "Songs")
                StatColumn("45", "Liked")
                StatColumn("678", "Listened")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    SecurePrefs.clearTokens(context)
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", color = Color.White)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Feature Not Implemented") },
                text = { Text("This feature is not available yet.") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

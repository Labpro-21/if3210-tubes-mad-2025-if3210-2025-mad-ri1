package com.example.pertamaxify.ui.main

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.ProfileResponse
import com.example.pertamaxify.data.model.ProfileViewModel
import com.example.pertamaxify.data.model.StatisticViewModel
import com.example.pertamaxify.data.remote.ApiClient
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.network.NetworkUtils
import com.example.pertamaxify.ui.network.NoConnectionScreen
import com.example.pertamaxify.ui.profile.LocationPickerScreen
import com.example.pertamaxify.ui.statistic.Capsule
import com.example.pertamaxify.ui.profile.ProfileUpdateDialog
import com.example.pertamaxify.utils.getCountryNameFromCode

@Composable
fun ProfileScreen(
    statisticViewModel: StatisticViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val token = SecurePrefs.getAccessToken(context)
    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showNoConnection by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf<Boolean>(false) }
    var newLocationCode by remember { mutableStateOf<String?>(null) }
    val isConnected by NetworkUtils.isConnected.collectAsState()
    val client = remember { ApiClient.instance }
    var email = ""

    val profileData by profileViewModel.profileData.collectAsState()

    LaunchedEffect(Unit) {
        NetworkUtils.registerNetworkCallback(context)
    }

    LaunchedEffect(isConnected) {
        Log.d("ProfileScreen", "isConnected=$isConnected | token=${token != null}")

        if (token == null && profile == null) {
            // Treat as guest
            profile = ProfileResponse(
                id = "-1",
                username = "Guest",
                email = "",
                profilePhoto = "dummy.png",
                location = "Unknown",
                createdAt = "",
                updatedAt = ""
            )

            showNoConnection = false
            return@LaunchedEffect
        }


        if (profile == null && isConnected) {
            try {
                val res = client.getProfile("Bearer $token")
                if (res.isSuccessful) {
                    profile = res.body()
                    showNoConnection = false

                    email = "${profile?.username}@std.stei.itb.ac.id"
                } else {
                    Log.e("ProfileScreen", "API Error: ${res.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Exception: ${e.localizedMessage}")
            }
        } else if (!isConnected && profile == null) {
            showNoConnection = true
        }

        profileViewModel.refreshData(email)
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
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd
            ) {
                val photoUrl = profile?.profilePhoto?.let {
                    "http://34.101.226.132:3000/uploads/profile-picture/$it"
                } ?: "http://34.101.226.132:3000/uploads/profile-picture/dummy.png"

                Image(
                    painter = rememberAsyncImagePainter(model = photoUrl),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile?.username ?: "Username",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = profile?.location?.let { countryCode ->
                    getCountryNameFromCode(countryCode)
                } ?: "Location has not been set",
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
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("123", "Songs")
                StatColumn("-", "Liked")
                StatColumn("678", "Listened")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    SecurePrefs.clearTokens(context)
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", color = Color.White)
            }

            if (!profile?.email.isNullOrBlank()) {
                Capsule(email = profile?.email, statisticViewModel = statisticViewModel)
            }
        }

        if (showMapPicker) {
            LocationPickerScreen(
                onLocationPicked = { _, _, countryCode ->
                    newLocationCode = countryCode
                    showMapPicker = false
                },
                onDismiss = {
                    showMapPicker = false
                    showDialog = true
                },
            )
        } else {
            if (showDialog) {
                ProfileUpdateDialog(
                    profileViewModel = profileViewModel,
                    onDismiss = {
                        showDialog = false },
                    profile = profile,
                    mapLocationCode = newLocationCode,
                    onShowMapClicked = { showMapPicker = true}
                )
            }
        }
    }
}

@Composable
fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, color = Color.White, style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

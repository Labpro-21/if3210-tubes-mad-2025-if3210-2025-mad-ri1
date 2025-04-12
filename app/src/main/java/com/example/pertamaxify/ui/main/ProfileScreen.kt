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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.pertamaxify.data.remote.AuthRepository
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.theme.WhiteText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<ProfileResponse?>(null) }

    LaunchedEffect(true) {
        val token = SecurePrefs.getAccessToken(context)
        if (!token.isNullOrEmpty()) {
            try {
                val response = withContext(Dispatchers.IO) {
                    AuthRepository().getProfile(token)
                }
                if (response.isSuccessful) {
                    profile = response.body()
                    Log.d("ProfileScreen", "Profile loaded: ${profile?.username}")
                } else {
                    Log.e("ProfileScreen", "Failed to fetch profile: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error fetching profile", e)
            }
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00667B),
            Color(0xFF002F38),
            Color(0xFF101010)
        ),
        startY = 0f,
        endY = 600f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = gradientBackground)
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 24.dp), // ⬅️ More top padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = rememberAsyncImagePainter("http://34.101.226.132:3000/uploads/profile-picture/${profile?.profilePhoto ?: "dummy.png"}"),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                IconButton(
                    onClick = { /* Handle edit */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = WhiteText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                profile?.username ?: "Username",
                style = MaterialTheme.typography.headlineSmall,
                color = WhiteText
            )
            Text(
                profile?.location ?: "Indonesia",
                style = MaterialTheme.typography.bodyMedium,
                color = WhiteText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Edit Profile */ }) {
                Text("Edit Profile")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ProfileStat("123", "Songs")
            ProfileStat("456", "Liked")
            ProfileStat("789", "Listened")
        }

        // ⬇️ Spacer to separate content from logout button
        Spacer(modifier = Modifier.height(64.dp))

        // ⬇️ Logout Button at Bottom
        Button(
            onClick = {
                SecurePrefs.clearTokens(context)
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = WhiteText)
        Text(label, style = MaterialTheme.typography.bodySmall, color = WhiteText)
    }
}

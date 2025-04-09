package com.example.pertamaxify.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.theme.BackgroundColor
import com.example.pertamaxify.utils.JwtUtils

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreenContent()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthentication()
        }, 2000) // 2-second delay
    }

    private fun checkAuthentication() {
        val accessToken = SecurePrefs.getAccessToken(this)
        val isTokenValid = accessToken?.let {
            (JwtUtils.decodeJwt(it)?.exp ?: 0) > (System.currentTimeMillis() / 1000)
        } ?: false

        val nextActivity = if (isTokenValid) HomeActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, nextActivity))
        finish()
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}

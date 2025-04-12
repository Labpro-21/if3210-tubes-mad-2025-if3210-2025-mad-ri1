package com.example.pertamaxify.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.example.pertamaxify.R
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.remote.AuthRepository
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.theme.BackgroundColor
import com.example.pertamaxify.utils.JwtUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    private val authRepository = AuthRepository()

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

        Log.d("SplashScreenActivity", "Checking authentication token.")

        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val currentTime = System.currentTimeMillis() / 1000

            if (jwtPayload != null && jwtPayload.exp > currentTime) {
                Log.d("SplashScreenActivity", "Token Found. Verifying with server...")

                lifecycleScope.launch(Dispatchers.IO) {
                    val isValid = authRepository.verifyToken(accessToken)

                    runOnUiThread {
                        if (isValid) {
                            Log.d(
                                "SplashScreenActivity",
                                "Token is valid. Proceeding to home screen."
                            )
                            openHomeScreen()
                        } else {
                            Log.d("SplashScreenActivity", "Token is invalid. Redirecting to login.")
                            openLoginScreen()
                        }
                    }
                }
                return
            }

            Log.d("SplashScreenActivity", "Invalid Token or Expired.")
        }

        Log.d("SplashScreenActivity", "No Token Found.")
        openLoginScreen()
    }

    private fun openHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun openLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
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

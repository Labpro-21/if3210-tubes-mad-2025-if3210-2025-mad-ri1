package com.example.pertamaxify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.splash.SplashScreenActivity
import com.example.pertamaxify.utils.JwtUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show splash screen only for API 29 & 30
        if (Build.VERSION.SDK_INT in 29..30) {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        } else {
            checkAuthentication()
        }
    }

    private fun checkAuthentication() {
        val accessToken = SecurePrefs.getAccessToken(this)

        Log.d("MainActivity" , "Checking authentication token.")

        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val currentTime = System.currentTimeMillis() / 1000

            if (jwtPayload != null && jwtPayload.exp > currentTime) {
                Log.d("MainActivity" , "Token Found.")
                openHomeScreen()
                return
            }

            Log.d("MainActivity" , "Invalid Token or Expired.")
        }

        Log.d("MainActivity" , "No Token Found.")
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

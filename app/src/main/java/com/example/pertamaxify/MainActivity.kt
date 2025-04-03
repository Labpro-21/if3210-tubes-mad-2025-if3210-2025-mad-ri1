package com.example.pertamaxify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.splash.SplashScreenActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show splash screen only for API 29 & 30
        if (Build.VERSION.SDK_INT in 29..30) {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish() // Close MainActivity until splash is done
        } else {
            openLoginScreen()
        }
    }

    private fun openLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Close MainActivity after launching LoginActivity
    }
}

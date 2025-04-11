package com.example.pertamaxify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.work.*
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.db.AppDatabase
import com.example.pertamaxify.db.DatabaseSeeder
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.splash.SplashScreenActivity
import com.example.pertamaxify.utils.JwtUtils
import com.example.pertamaxify.workers.TokenRefreshWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the background worker
        startTokenRefreshWorker()

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

        Log.d("MainActivity", "Checking authentication token.")

        if (!accessToken.isNullOrEmpty()) {
            val jwtPayload = JwtUtils.decodeJwt(accessToken)
            val currentTime = System.currentTimeMillis() / 1000

            if (jwtPayload != null && jwtPayload.exp > currentTime) {
                Log.d("MainActivity", "Token Found.")
                DatabaseSeeder.seedSong(applicationContext, database) {
                    openHomeScreen()
                }
                return
            }

            Log.d("MainActivity", "Invalid Token or Expired.")
        }

        Log.d("MainActivity", "No Token Found.")
        DatabaseSeeder.seedSong(applicationContext, database) {
            openLoginScreen()
        }
    }

    private fun startTokenRefreshWorker() {
        Log.d("MainActivity", "Scheduling TokenRefreshWorker...")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(5, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        Log.d("MainActivity", "TokenRefreshWorker scheduled successfully!")
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

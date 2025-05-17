package com.example.pertamaxify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.model.User
import com.example.pertamaxify.data.remote.AuthRepository
import com.example.pertamaxify.db.AppDatabase
import com.example.pertamaxify.db.DatabaseSeeder
import com.example.pertamaxify.ui.auth.LoginActivity
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.splash.SplashScreenActivity
import com.example.pertamaxify.utils.JwtUtils
import com.example.pertamaxify.ui.network.NetworkUtils
import com.example.pertamaxify.workers.TokenRefreshWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: AppDatabase

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for network connection
        if (!NetworkUtils.isNetworkConnected(this)) {
            // Log the network issue and go to login screen
            Log.e("MainActivity", "No internet connection. Redirecting to login.")
            openLoginScreen()
            return
        }

        // Start the background worker
        startTokenRefreshWorker()

        // Show splash screen only for API 29 & 30
        if (Build.VERSION.SDK_INT in 29..30) {
            DatabaseSeeder.seedSong(applicationContext, database) {

            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
            }
        } else {
            DatabaseSeeder.seedSong(applicationContext, database) {
                checkAuthentication()
            }
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
                // Seed
                DatabaseSeeder.seedSong(applicationContext, database) {
                    openHomeScreen()
                }
                Log.d("MainActivity", "Token Found. Verifying with server...")

                lifecycleScope.launch(Dispatchers.IO) {
                    val isValid = authRepository.verifyToken(accessToken)

                    if (isValid) {
                        Log.d("MainActivity", "Token is valid. Proceeding to home screen.")

                        // Insert the user data into the database
                        database.userDao().upsertUser(
                            User(
                                username = jwtPayload.username,
                                email = "${jwtPayload.username}@std.stei.itb.ac.id",
                                imageProfile = null
                            )
                        )
                        openHomeScreen()
                    } else {
                        Log.d("MainActivity", "Token is invalid. Redirecting to login.")
                        openLoginScreen()
                    }
                }
                return
            }

            Log.d("MainActivity", "Invalid Token or Expired.")
        }

        Log.d("MainActivity", "No Token Found.")
        // Seed
        DatabaseSeeder.seedSong(applicationContext, database) {
            openLoginScreen()
        }
    }

    private fun startTokenRefreshWorker() {
        Log.d("MainActivity", "Scheduling TokenRefreshWorker...")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TokenRefreshWorker>()
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "TokenRefreshWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Log.d("MainActivity", "TokenRefreshWorker scheduled successfully!")
    }

    private fun openHomeScreen() {
        runOnUiThread {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun openLoginScreen() {
        runOnUiThread {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

package com.example.pertamaxify.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.data.remote.AuthRepository
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class TokenRefreshWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val authRepository = AuthRepository()

    override fun doWork(): Result {
        Log.d("TokenRefreshWorker", "Worker started. Checking token validity...")

//        val currentToken = SecurePrefs.getAccessToken(applicationContext)
        val refreshToken = SecurePrefs.getRefreshToken(applicationContext)

        if (refreshToken.isNullOrEmpty()) {
            Log.e("TokenRefreshWorker", "No refresh token found! Stopping worker.")
            return Result.failure()
        }

        val result = runBlocking {
            try {
                val response = authRepository.refreshToken(refreshToken)

                if (response.isSuccessful && response.body() != null) {
                    val newAccessToken = response.body()!!.accessToken
                    val newRefreshToken = response.body()!!.refreshToken

                    SecurePrefs.saveTokens(applicationContext, newAccessToken, newRefreshToken)
                    Log.d("TokenRefreshWorker", "Tokens updated successfully!")

                    Result.success()
                } else {
                    Log.e("TokenRefreshWorker", "Token refresh failed! Response: ${response.code()}")
                    Result.failure()
                }
            } catch (e: Exception) {
                Log.e("TokenRefreshWorker", "Error refreshing token: ${e.message}")
                Result.retry()
            }
        }

        scheduleNextRun(applicationContext)

        return result
    }

    private fun scheduleNextRun(context: Context) {
        Log.d("TokenRefreshWorker", "Scheduling next token refresh in 5 minutes...")

        val workRequest = OneTimeWorkRequestBuilder<TokenRefreshWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

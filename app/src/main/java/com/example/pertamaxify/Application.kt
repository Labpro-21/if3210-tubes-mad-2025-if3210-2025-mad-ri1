package com.example.pertamaxify

import android.app.Application
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import android.app.NotificationChannel
import android.app.NotificationManager

const val CHANNEL_ID = "pertamaxify_channel"
const val CHANNEL_NAME = "Pertamaxify Channel"
const val CHANNEL_DESCRIPTION = "Pertamaxify Notification Channel"

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Create notification channel for music player
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = CHANNEL_DESCRIPTION
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
package com.example.pertamaxify.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object SecurePrefs {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKey =
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Save Tokens
    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
        Log.d("SecurePrefs", "Tokens saved securely!")
    }

    // Get Access Token
    fun getAccessToken(context: Context): String? =
        getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)

    // Get Refresh Token
    fun getRefreshToken(context: Context): String? =
        getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null)

    // Clear Tokens
    fun clearTokens(context: Context) {
        getSharedPreferences(context).edit { clear() }
        Log.d("SecurePrefs", "Tokens cleared!")
    }
}

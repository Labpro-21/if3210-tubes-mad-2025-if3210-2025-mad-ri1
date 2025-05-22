package com.example.pertamaxify.data.repository

import android.util.Log
import com.example.pertamaxify.data.model.SongResponse
import com.example.pertamaxify.data.remote.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor() {
    private val songService = ApiClient.songService

    // List of supported country codes
    val supportedCountries = listOf("ID", "MY", "US", "GB", "CH", "DE", "BR")

    // Country names for display
    val countryNames = mapOf(
        "ID" to "Indonesia",
        "MY" to "Malaysia",
        "US" to "United States",
        "GB" to "United Kingdom",
        "CH" to "Switzerland",
        "DE" to "Germany",
        "BR" to "Brazil"
    )

    /**
     * Get global top songs
     */
    suspend fun getGlobalTopSongs(): List<SongResponse> {
        return try {
            val response = songService.getGlobalTopSongs()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("PlaylistRepository", "Error getting global top songs: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Exception getting global top songs", e)
            emptyList()
        }
    }

    /**
     * Get top songs for a specific country
     */
    suspend fun getCountryTopSongs(countryCode: String): List<SongResponse> {
        return try {
            // Check if country code is supported
            if (countryCode !in supportedCountries) {
                Log.e("PlaylistRepository", "Unsupported country code: $countryCode")
                return emptyList()
            }

            val response = songService.getCountryTopSongs(countryCode)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("PlaylistRepository", "Error getting country top songs: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Exception getting country top songs", e)
            emptyList()
        }
    }

    /**
     * Get song by ID
     */
    suspend fun getSongById(songId: Int): SongResponse? {
        return try {
            val response = songService.getSongById(songId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PlaylistRepository", "Error getting song by ID: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Exception getting song by ID", e)
            null
        }
    }
}

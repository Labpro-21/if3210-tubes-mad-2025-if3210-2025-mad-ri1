package com.example.pertamaxify.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility class for extracting metadata from audio files
 */
object AudioMetadataExtractor {

    data class AudioMetadata(
        val title: String? = null,
        val artist: String? = null,
        val albumArt: Uri? = null,
        val duration: Long = 0
    )

    /**
     * Extract metadata from an audio file
     * @param context Application context
     * @param audioUri URI of the audio file
     * @return AudioMetadata object containing the extracted metadata, or null if extraction failed
     */
    suspend fun extractMetadata(context: Context, audioUri: Uri): AudioMetadata? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, audioUri)

            // Extract basic metadata
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationStr?.toLongOrNull() ?: 0L

            // Extract album art
            val albumArtUri = extractAlbumArt(context, retriever)

            // Create and return metadata object
            AudioMetadata(
                title = title,
                artist = artist,
                albumArt = albumArtUri,
                duration = duration
            )
        } catch (e: Exception) {
            Log.e("AudioMetadataExtractor", "Error extracting metadata: ${e.message}")
            null
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                Log.e("AudioMetadataExtractor", "Error releasing MediaMetadataRetriever: ${e.message}")
            }
        }
    }

    /**
     * Extract album art from the media and save it to a temporary file
     * @param context Application context
     * @param retriever Media metadata retriever with data source already set
     * @return URI of the extracted album art image, or null if no album art found or extraction failed
     */
    private fun extractAlbumArt(context: Context, retriever: MediaMetadataRetriever): Uri? {
        try {
            // Get embedded album art as a byte array
            val artBytes = retriever.embeddedPicture ?: return null

            // Convert to bitmap
            val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
            if (bitmap == null) {
                Log.e("AudioMetadataExtractor", "Failed to decode album art bitmap")
                return null
            }

            // Create temporary file to store the album art
            val tempFile = File(context.cacheDir, "album_art_${System.currentTimeMillis()}.jpg")
            var fileOutputStream: FileOutputStream? = null

            try {
                fileOutputStream = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
            } catch (e: IOException) {
                Log.e("AudioMetadataExtractor", "Error saving album art: ${e.message}")
                return null
            } finally {
                fileOutputStream?.close()
            }

            // Return URI to the saved file
            return Uri.fromFile(tempFile)
        } catch (e: Exception) {
            Log.e("AudioMetadataExtractor", "Error extracting album art: ${e.message}")
            return null
        }
    }

    /**
     * Get just the title from an audio file (without extracting other metadata)
     */
    fun getAudioTitle(context: Context, audioUri: Uri): String? {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, audioUri)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        } catch (e: Exception) {
            Log.e("AudioMetadataExtractor", "Error getting title: ${e.message}")
            return null
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                Log.e("AudioMetadataExtractor", "Error releasing MediaMetadataRetriever: ${e.message}")
            }
        }
    }

    /**
     * Get just the artist from an audio file (without extracting other metadata)
     */
    fun getAudioArtist(context: Context, audioUri: Uri): String? {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, audioUri)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
        } catch (e: Exception) {
            Log.e("AudioMetadataExtractor", "Error getting artist: ${e.message}")
            return null
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                Log.e("AudioMetadataExtractor", "Error releasing MediaMetadataRetriever: ${e.message}")
            }
        }
    }
}

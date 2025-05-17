package com.example.pertamaxify.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

/**
 * Helper class for handling URIs in a way that works across different Android versions
 */
object UriHelper {
    /**
     * Safely tries to take persistent URI permissions if needed
     */
    fun safeTakePersistentPermission(context: Context, uriString: String) {
        try {
            val uri = uriString.toUri()
            if (needsPersistentPermission(uri)) {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            }
        } catch (e: Exception) {
            // Just log the error and continue - we'll handle access differently if needed
            Log.e("UriHelper", "Error taking persistent permission for URI: $uriString", e)
        }
    }

    /**
     * Checks if a URI needs persistent permissions
     */
    private fun needsPersistentPermission(uri: Uri): Boolean {
        val scheme = uri.scheme ?: return false
        return scheme == ContentResolver.SCHEME_CONTENT &&
                (uri.authority.orEmpty().contains("com.android.providers.downloads.documents") ||
                        uri.authority.orEmpty().contains("com.android.externalstorage.documents"))
    }

    /**
     * Verifies if a URI is still valid and accessible
     */
    fun isUriValid(context: Context, uriString: String): Boolean {
        return try {
            val uri = uriString.toUri()
            val contentResolver = context.contentResolver

            // First attempt: try to query metadata without opening file
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                return cursor.count > 0
            }

            // Second attempt: try to get file descriptor
            try {
                contentResolver.openFileDescriptor(uri, "r")?.close()
                true
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            Log.e("UriHelper", "URI is no longer valid: $uriString", e)
            false
        }
    }

    /**
     * Gets file metadata from a URI like filename, size, etc.
     */
    fun getFileInfo(context: Context, uri: Uri): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()

        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
            documentFile?.let {
                result["name"] = it.name
                result["type"] = it.type
                result["size"] = it.length()
                result["lastModified"] = it.lastModified()
            }

            // Try to get more metadata from MediaStore for media files
            val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_MODIFIED
            )

            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                val mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                val dateModifiedIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)

                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) result["name"] = cursor.getString(nameIndex)
                    if (sizeIndex != -1) result["size"] = cursor.getLong(sizeIndex)
                    if (mimeTypeIndex != -1) result["mimeType"] = cursor.getString(mimeTypeIndex)
                    if (dateModifiedIndex != -1) result["dateModified"] = cursor.getLong(dateModifiedIndex)
                }
            }
        } catch (e: Exception) {
            Log.e("UriHelper", "Error getting file info for URI: $uri", e)
        }

        return result
    }

    /**
     * Adds the necessary intent flags to ensure a file can be opened
     */
    fun createAudioPlayIntent(uri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}

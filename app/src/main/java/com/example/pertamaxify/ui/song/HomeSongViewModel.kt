package com.example.pertamaxify.ui.song

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore

fun getImageUriFromDownloads(context: Context, fileName: String): Uri? {
    val projection = arrayOf(
        MediaStore.Downloads._ID,
        MediaStore.Downloads.DISPLAY_NAME
    )

    val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(fileName)

    val queryUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    context.contentResolver.query(
        queryUri,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)

        if (cursor.moveToFirst()) {
            val id = cursor.getLong(idColumn)
            return ContentUris.withAppendedId(queryUri, id)
        }
    }
    return null
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

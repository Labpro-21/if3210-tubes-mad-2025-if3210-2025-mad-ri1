package com.example.pertamaxify.ui.library

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pertamaxify.R
import com.example.pertamaxify.ui.song.UploadBox
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText
import com.example.pertamaxify.utils.AudioMetadataExtractor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String?, String, String?) -> Unit, // title, artist, imagePath, audioPath, email
    email: String?
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }

    // State to track if metadata extraction is in progress
    var isExtracting by remember { mutableStateOf(false) }

    // Coroutine scope for metadata extraction
    val coroutineScope = rememberCoroutineScope()

    // Use OpenDocument for getting persistable permissions
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Take persistable URI permission for the image
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                imageUri = uri
            } catch (e: Exception) {
                Log.e("AddSongDialog", "Error taking persistable permission for image URI: $uri", e)
                // Store the URI anyway, we'll handle access differently
                imageUri = uri
            }
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Take persistable URI permission for the audio
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                audioUri = uri

                // Extract metadata when audio is selected
                isExtracting = true
                coroutineScope.launch {
                    val metadata = AudioMetadataExtractor.extractMetadata(context, uri)

                    Log.d("AddSongDialog", "Extracted metadata: $metadata")

                    // Auto-fill fields with extracted metadata if available
                    metadata?.let { meta ->
                        // Only set title if it's not empty and the user hasn't entered anything yet
                        if (!meta.title.isNullOrBlank() && title.isBlank()) {
                            title = meta.title
                        }

                        // Only set artist if it's not empty and the user hasn't entered anything yet
                        if (!meta.artist.isNullOrBlank() && artist.isBlank()) {
                            artist = meta.artist
                        }

                        // Set album art if available and no image is selected yet
                        if (meta.albumArt != null && imageUri == null) {
                            imageUri = meta.albumArt
                        }
                    }

                    isExtracting = false
                }
            } catch (e: Exception) {
                Log.e("AddSongDialog", "Error taking persistable permission for audio URI: $uri", e)
                // Store the URI anyway, we'll handle access differently
                audioUri = uri
                isExtracting = false
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1C1C1E)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text("Upload Song", style = Typography.titleLarge, color = WhiteText)

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UploadBox(
                        label = imageUri?.let { "Image Selected" } ?: "Upload Photo",
                        icon = R.drawable.placeholder,
                        onClick = {
                            imagePickerLauncher.launch(arrayOf("image/*"))
                        },
                        imageUri = imageUri
                    )
                    UploadBox(
                        label = audioUri?.let { "Audio Selected" } ?: "Upload File",
                        icon = R.drawable.placeholder,
                        onClick = {
                            audioPickerLauncher.launch(arrayOf("audio/*"))
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Show loading indicator while extracting metadata
                if (isExtracting) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extracting metadata...", color = Color.White)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text("Title", color = Color.White)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(16.dp))

                Text("Artist", color = Color.White)
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    placeholder = { Text("Artist") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && artist.isNotBlank() &&
                                audioUri != null) {

                                // Store URIs directly as strings
                                val imageUriString = imageUri.toString()
                                val audioUriString = audioUri.toString()

                                Log.d("URI:", "Image: $imageUriString, Audio: $audioUriString")
                                onSave(title, artist, imageUriString, audioUriString, email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier.weight(1f),
                        enabled = !isExtracting && title.isNotBlank() && artist.isNotBlank() &&
                                audioUri != null
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}

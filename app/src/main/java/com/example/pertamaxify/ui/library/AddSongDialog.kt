package com.example.pertamaxify.ui.library

import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.AddSongModel
import com.example.pertamaxify.ui.song.UploadBox
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit,
    viewModel: AddSongModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var audioUri by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { audioUri = it.toString() }
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
                        onClick = { imagePickerLauncher.launch("image/*") }
                    )
                    UploadBox(
                        label = audioUri?.let { "Audio Selected" } ?: "Upload File",
                        icon = R.drawable.placeholder,
                        onClick = { audioPickerLauncher.launch("audio/*") }
                    )
                }

                Spacer(Modifier.height(24.dp))

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
                                imageUri != null && audioUri != null) {
                                onSave(title, artist, imageUri!!, audioUri!!)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}
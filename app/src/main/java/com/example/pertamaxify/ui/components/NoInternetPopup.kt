package com.example.pertamaxify.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pertamaxify.utils.NetworkUtils
import kotlinx.coroutines.delay

@Composable
fun NoInternetPopup(context: android.content.Context) {
    var showPopup by remember { mutableStateOf(!NetworkUtils.isInternetAvailable(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            showPopup = !NetworkUtils.isInternetAvailable(context)
        }
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("OK")
                }
            },
            title = { Text("No Internet Connection") },
            text = { Text("Please check your internet connection.") }
        )
    }
}

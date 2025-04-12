package com.example.pertamaxify.ui.main

import android.graphics.fonts.FontStyle
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.library.AddSongDialog
import com.example.pertamaxify.ui.library.LibraryFragment
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText

@Preview
@Composable
fun LibraryScreen(viewModel: LibraryViewModel = hiltViewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp, 24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Your Library",
                modifier = Modifier.padding(16.dp),
                color = WhiteText,
                style = Typography.titleLarge
            )
            Text(
                "+",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDialog = true },
                color = WhiteText,
                style = Typography.titleLarge
            )
        }

        if (showDialog) {
            AddSongDialog(
                onDismiss = { showDialog = false },
                onSave = { title, artist, imagePath, audioPath ->
                    viewModel.saveSong(
                        Song(
                            title = title,
                            singer = artist,
                            imagePath = imagePath,
                            audioPath = audioPath
                        )
                    )
                    showDialog = false
                }
            )
        }
        // Use AndroidView to host a fragment container
        Log.d("LibraryScreen", "LibraryScreen: Composable function called")
//    AndroidView(factory = { context ->
//        // Inflate the container view
//        val inflater = LayoutInflater.from(context)
//        val container = inflater.inflate(R.layout.fragment_container_library, null)
//        Log.d("LibraryScreen", "LibraryScreen: Inflated container view")
//
//        // Obtain the FragmentActivity (if the context is not a FragmentActivity, it won't work)
//        if (context is FragmentActivity) {
//            Log.d("LibraryScreen", "LibraryScreen: Context is FragmentActivity")
//            val fm = context.supportFragmentManager
//            // Check if our fragment is already added
//            val existingFragment = fm.findFragmentById(R.id.library_fragment_container)
//            if (existingFragment == null) {
//                fm.beginTransaction()
//                    .replace(R.id.library_fragment_container, LibraryFragment())
//                    .commitNow() // commitNow ensures the fragment is immediately added
//            }
//        }
//
//        container
//    })
    }
}
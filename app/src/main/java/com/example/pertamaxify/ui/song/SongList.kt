package com.example.pertamaxify.ui.song

import SongAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.data.model.Song

@Composable
fun SongListRecyclerView(
    songs: List<Song>,
    onToggleLike: (Song) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            // Create RecyclerView
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SongAdapter(onToggleLike)
            }
        },
        update = { recyclerView ->
            // Update adapter’s list when `songs` changes
            val songAdapter = recyclerView.adapter as SongAdapter
            songAdapter.submitList(songs)
        }
    )
}

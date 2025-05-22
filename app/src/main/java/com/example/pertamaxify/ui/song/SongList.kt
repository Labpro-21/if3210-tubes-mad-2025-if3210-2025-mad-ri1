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
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SongAdapter()
            }
        },
        update = { recyclerView ->
            val songAdapter = (recyclerView.adapter as? SongAdapter)
                ?: SongAdapter().also { recyclerView.adapter = it }
            songAdapter.submitList(songs)
        }
    )
}


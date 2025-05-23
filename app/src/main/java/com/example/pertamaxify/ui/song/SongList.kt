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
    onSongClick: ((Song) -> Unit)? = null,
    onSongLongClick: ((Song) -> Unit)? = null
) {
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SongAdapter().apply {
                    onSongClick?.let { setOnSongClickListener(it) }
                    onSongLongClick?.let { setOnSongLongClickListener(it) }
                }
            }
        },
        update = { recyclerView ->
            val songAdapter = (recyclerView.adapter as? SongAdapter)
                ?: SongAdapter().also {
                    recyclerView.adapter = it
                    onSongClick?.let { callback -> it.setOnSongClickListener(callback) }
                    onSongLongClick?.let { callback -> it.setOnSongLongClickListener(callback) }
                }
            songAdapter.submitList(songs)
        }
    )
}
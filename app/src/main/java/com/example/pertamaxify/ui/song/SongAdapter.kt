package com.example.pertamaxify.ui.song

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.data.model.Song

class SongAdapter(
    private val onLongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val items = mutableListOf<Song>()

    fun submitList(songs: List<Song>) {
        items.clear()
        items.addAll(songs)
        notifyDataSetChanged()
    }

    class SongViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        // Create a ComposeView programmatically
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return SongViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = items[position]
        holder.composeView.setContent {
            SongItem (song, onLongClick)
        }
    }

    override fun getItemCount(): Int = items.size
}
package com.example.pertamaxify.ui.song

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.R
import coil.load
import com.example.pertamaxify.data.model.Song
import java.io.File

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.findViewById<TextView>(R.id.songTitle)
    private val singerTextView = itemView.findViewById<TextView>(R.id.songSinger)
    private val imageView = itemView.findViewById<ImageView>(R.id.songArtwork)

    fun bind(song: Song,
             onSongClick: ((Song) -> Unit)? = null,
             onLongClick: ((Song) -> Unit)? = null,
             ) {
        titleTextView.text = song.title
        singerTextView.text = song.artist

        if (song.artwork.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.song_image_placeholder) // Placeholder image
        } else {
            val file = File(song.artwork.removePrefix("file://"))
            imageView.load(file) {
                crossfade(true)
                placeholder(R.drawable.song_image_placeholder)
                error(R.drawable.song_image_placeholder)
            }
        }
        itemView.setOnClickListener{
            onSongClick?.invoke(song)
        }

        itemView.setOnLongClickListener {
            onLongClick?.invoke(song)
            true
        }
    }
}


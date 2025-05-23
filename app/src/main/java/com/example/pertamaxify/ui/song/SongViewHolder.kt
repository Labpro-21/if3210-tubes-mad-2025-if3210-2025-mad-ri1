package com.example.pertamaxify.ui.song

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song

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
        // Load image using your preferred image loading library
//        if (song.artwork != null) {
//            imageView.setImageResource(
//
//            )
//        } else {
//            imageView.setImageResource(R.drawable.song_image_placeholder) // Placeholder image
//        }
        itemView.setOnClickListener{
            onSongClick?.invoke(song)
        }

        itemView.setOnLongClickListener {
            onLongClick?.invoke(song)
            true
        }
    }
}


package com.example.pertamaxify.ui.song

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.findViewById<TextView>(R.id.songTitle)
    private val singerTextView = itemView.findViewById<TextView>(R.id.songSinger)
    val optionsButton: TextView = itemView.findViewById(R.id.optionsButton)

    fun bind(song: Song) {
        titleTextView.text = song.title
        singerTextView.text = song.singer
    }
}


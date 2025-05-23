import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.song.SongViewHolder


class SongAdapter : RecyclerView.Adapter<SongViewHolder>() {

    private val songs = mutableListOf<Song>()
    private var onSongClickListener: ((Song) -> Unit)? = null
    private var onSongLongClickListener: ((Song) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(
            song = song,
            onSongClick = { onSongClickListener?.invoke(it) },
            onLongClick = { showPopupMenu(holder.itemView, it) }
        )
    }

    override fun getItemCount(): Int = songs.size

    fun submitList(newSongs: List<Song>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    fun setOnSongClickListener(listener: (Song) -> Unit) {
        onSongClickListener = listener
    }

    fun setOnSongLongClickListener(listener: (Song) -> Unit) {
        onSongLongClickListener = listener
    }

    private fun showPopupMenu(view: android.view.View, song: Song) {
        val popup = PopupMenu(view.context, view)

        // Add menu items
        if (song.isLiked == true) {
            popup.menu.add("Remove from Liked")
        } else {
            popup.menu.add("Add to Liked")
        }
        popup.menu.add("Delete Song")

        // Handle menu selection
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title.toString()) {
                "Add to Liked", "Remove from Liked" -> {
                    onSongLongClickListener?.invoke(song)
                }
                "Delete Song" -> {
                    // Handle delete - you can add a separate callback for this
                    onSongLongClickListener?.invoke(song)
                }
            }
            true
        }
        popup.show()
    }
}
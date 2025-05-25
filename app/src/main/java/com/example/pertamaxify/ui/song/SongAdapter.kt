import android.view.LayoutInflater
import android.view.ViewGroup
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
            onLongClick = { onSongLongClickListener?.invoke(it) } // Just invoke callback
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
}

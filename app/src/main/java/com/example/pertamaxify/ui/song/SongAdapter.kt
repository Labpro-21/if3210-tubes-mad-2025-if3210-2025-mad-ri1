import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.song.SongViewHolder

class SongAdapter : RecyclerView.Adapter<SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)

        // Long press to show popup menu
        holder.itemView.setOnLongClickListener { view ->
            val popup = PopupMenu(view.context, view)
            // If song is liked, show "Remove from Liked" or else "Add to Liked"
            if (song.isLiked == true) {
                popup.menu.add("Remove from Liked")
            } else {
                popup.menu.add("Add to Liked")
            }

            // Handle menu selection
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    // Contect menu
                }
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = songs.size

    fun submitList(newSongs: List<Song>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }
}

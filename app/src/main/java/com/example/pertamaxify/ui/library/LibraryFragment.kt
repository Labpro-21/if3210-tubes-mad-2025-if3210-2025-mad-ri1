package com.example.pertamaxify.ui.library

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.LibraryViewModel
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.song.SongAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.library_fragment) {

    private val viewModel: LibraryViewModel by viewModels()

    private lateinit var allSongsAdapter: SongAdapter
    private lateinit var likedSongsAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LibraryFragment", "onViewCreated: LibraryFragment created")

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val recyclerAll = view.findViewById<RecyclerView>(R.id.recycler_all_songs)
        val recyclerLiked = view.findViewById<RecyclerView>(R.id.recycler_liked_songs)

        recyclerAll.layoutManager = LinearLayoutManager(requireContext())
        recyclerLiked.layoutManager = LinearLayoutManager(requireContext())

        allSongsAdapter = SongAdapter(onLongClick = { song ->
            showContextMenu(song)
        })

        likedSongsAdapter = SongAdapter(onLongClick = { song ->
            showContextMenu(song)
        })

        recyclerAll.adapter = allSongsAdapter
        recyclerLiked.adapter = likedSongsAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.allSongs.collect { songs ->
                        allSongsAdapter.submitList(songs)
                    }
                }
                launch {
                    viewModel.likedSongs.collect { songs ->
                        likedSongsAdapter.submitList(songs)
                    }
                }
            }
        }

        // Tab selection logic
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Show all songs container, hide liked
                        recyclerAll.visibility = View.VISIBLE
                        recyclerLiked.visibility = View.GONE
                    }
                    1 -> {
                        // Show liked songs container, hide all
                        recyclerAll.visibility = View.GONE
                        recyclerLiked.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Initialize tabs
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Liked"))
        // By default, show the "All" tab
        recyclerAll.visibility = View.VISIBLE
        recyclerLiked.visibility = View.GONE

        // Force initial fetch
        viewModel.fetchAllSongs()
        viewModel.fetchLikedSongs()
    }

    private fun showContextMenu(song: Song) {
        // Traditional PopupMenu or BottomSheet
        val popup = PopupMenu(requireContext(), requireView())
        popup.menuInflater.inflate(R.menu.song_context_menu, popup.menu)
        val label = if (song.isLiked == true) "Remove from Liked" else "Add to Liked"
        popup.menu.findItem(R.id.action_like_unlike).title = label

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_like_unlike -> {
                    viewModel.toggleLike(song)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}

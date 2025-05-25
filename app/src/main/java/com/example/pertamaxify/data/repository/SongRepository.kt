package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.db.SongDao
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songDao: SongDao,
) {
    fun getAllSongs(): List<Song> = songDao.getAllSong()

    fun getAllSongsByEmail(email: String): List<Song> = songDao.getAllSongByEmail(email)

    fun getSongByTitle(title: String): List<Song> = songDao.getSongByTitle(title)

    fun getSongById(id: Int): Song = songDao.getSongById(id)

    fun getAllLikedSongs(): List<Song> = songDao.getAllLikedSong()

    fun getAllLikedSongsByEmail(email: String): List<Song> = songDao.getAllLikedSongByEmail(email)

    fun getRecentlyAddedSongs(): List<Song> = songDao.getRecentlyAddedSongs()

    fun getRecentlyAddedSongsByUser(email: String): List<Song> = songDao.getRecentlyAddedSongsByUser(email)

    fun getAllDownloadedSongs(): List<Song> = songDao.getAllDownloadedSongs()

    fun getAllDownloadedSongsByEmail(email: String): List<Song> = songDao.getAllDownloadedSongsByEmail(email)

    fun getAllSongsByArtist(artist: String): List<Song> = songDao.getAllSongsByArtist(artist)

    suspend fun upsertSong(song: Song) {
        songDao.upsertSong(song)
    }

    suspend fun deleteSong(song: Song) {
        songDao.deleteSong(song)
    }

    suspend fun updateSong(song: Song) {
        songDao.updateSong(song)
    }

    suspend fun insertSong(song: Song) {
        songDao.insertSong(song)
    }
}

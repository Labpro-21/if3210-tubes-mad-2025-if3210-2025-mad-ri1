package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.db.SongDao
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songDao: SongDao
) {
    fun getAllSongs(): List<Song> = songDao.getAllSong()

    fun getSongByTitle(title: String): List<Song> = songDao.getSongByTitle(title)

    fun getSongById(id: String): Song = songDao.getSongById(id)

    fun getAllLikedSongs(): List<Song> = songDao.getAllLikedSong()

    fun getRecentlyPlayedSongs(): List<Song> = songDao.getRecentlyPlayedSongs()

    fun getRecentlyPlayedSongsByUser(email: String): List<Song> = songDao.getRecentlyPlayedSongsByUser(email)

    fun getRecentlyAddedSongs(): List<Song> = songDao.getRecentlyAddedSongs()

    fun getRecentlyAddedSongsByUser(email: String): List<Song> = songDao.getRecentlyAddedSongsByUser(email)

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

    // Specific for an user
    fun getAllSongsByUser(username: String): List<Song> = songDao.getAllSongByUser(username)
    fun getAllLikedSongsByUser(username: String): List<Song> = songDao.getAllLikedSongByUser(username)
}

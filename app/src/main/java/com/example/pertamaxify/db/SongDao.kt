package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.pertamaxify.data.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM song ORDER BY addedTime DESC")
    fun getAllSong() : List<Song>

    @Query("SELECT * FROM song WHERE addedBy = :email ORDER BY addedTime DESC")
    fun getAllSongByEmail(email: String): List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :title")
    fun getSongByTitle(title: String): List<Song>

    @Query("SELECT * FROM song WHERE id = :id")
    fun getSongById(id: Int): Song

    @Query("SELECT * FROM song WHERE isLiked = 1 ORDER BY addedTime DESC")
    fun getAllLikedSong(): List<Song>

    @Query("SELECT * FROM song WHERE isLiked = 1 AND addedBy = :email ORDER BY addedTime DESC")
    fun getAllLikedSongByEmail(email: String): List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :title LIMIT 1")
    fun getSong(title: String): Song

    @Query("SELECT * FROM song WHERE addedBy IS NOT NULL ORDER BY addedTime DESC LIMIT 20")
    fun getRecentlyAddedSongs(): List<Song>

    @Query("SELECT * FROM song WHERE addedBy = :email AND addedTime IS NOT NULL ORDER BY addedTime DESC LIMIT 20")
    fun getRecentlyAddedSongsByUser(email: String): List<Song>

    @Query("SELECT * FROM song WHERE isDownloaded = 1 ORDER BY addedTime DESC")
    fun getAllDownloadedSongs(): List<Song>

    @Query("SELECT * FROM song WHERE isDownloaded = 1 AND addedBy = :email ORDER BY addedTime DESC")
    fun getAllDownloadedSongsByEmail(email: String): List<Song>

    @Query("SELECT * FROM song WHERE artist = :artist ORDER BY addedTime DESC")
    fun getAllSongsByArtist(artist: String): List<Song>

    @Upsert
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Update
    suspend fun updateSong(song: Song)

    @Insert
    suspend fun insertSong(song: Song)

    // Specific for an user
    @Query("SELECT * FROM song WHERE addedBy = :username ORDER BY addedTime DESC")
    fun getAllSongByUser(username: String): List<Song>

    @Query("SELECT * FROM song WHERE addedBy = :username AND isLiked = 1 ORDER BY addedTime DESC")
    fun getAllLikedSongByUser(username: String): List<Song>
}

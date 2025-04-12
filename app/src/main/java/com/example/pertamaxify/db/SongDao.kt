package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.pertamaxify.data.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSong() : List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :title")
    fun getSongByTitle(title: String): List<Song>

    @Query("SELECT * FROM song WHERE id = :id")
    fun getSongById(id: String): Song

    @Query("SELECT * FROM song WHERE isLiked = 1")
    fun getAllLikedSong(): List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :title LIMIT 1")
    fun getSong(title: String): Song

    @Upsert
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Update
    suspend fun updateSong(song: Song)

    // Specific for an user
    @Query("SELECT * FROM song WHERE addedBy = :username")
    fun getAllSongByUser(username: String): List<Song>

    @Query("SELECT * FROM song WHERE addedBy = :username AND isLiked = 1")
    fun getAllLikedSongByUser(username: String): List<Song>
}
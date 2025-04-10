package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.pertamaxify.data.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM Song")
    fun getAllSong() : List<Song>

    @Query("SELECT * FROM Song WHERE title LIKE :title")
    fun getSongByTitle(title: String): List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :title LIMIT 1")
    fun getSong(title: String): Song

    @Upsert
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)
}
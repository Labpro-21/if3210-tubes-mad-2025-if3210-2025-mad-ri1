package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Query
import com.example.pertamaxify.data.model.LikedSong

@Dao
interface LikedSongDao {
    @Query("SELECT * FROM LikedSong")
    fun getAllLikedSong(): List<LikedSong>
}
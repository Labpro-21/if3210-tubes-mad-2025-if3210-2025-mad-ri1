package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PlayedSongUserDao {
    @Query("SELECT * FROM PlayedSongUser")
    fun getAllPlayedSongbyUser(): List<PlayedSongUserDao>
}
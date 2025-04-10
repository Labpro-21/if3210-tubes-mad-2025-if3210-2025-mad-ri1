package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Query
import com.example.pertamaxify.data.model.PlayedSongUser

@Dao
interface PlayedSongUserDao {
    @Query("SELECT * FROM PlayedSongUser")
    fun getAllPlayedSongbyUser(): List<PlayedSongUser>
}
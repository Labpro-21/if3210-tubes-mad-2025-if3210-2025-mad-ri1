package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUser(): List<User>

    @Upsert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(song: Song)
}
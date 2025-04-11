package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Query
import com.example.pertamaxify.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUser(): List<User>
}
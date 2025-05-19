package com.example.pertamaxify.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.pertamaxify.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUser(): List<User>

    @Upsert
    fun upsertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): User

    @Query("SELECT * FROM user WHERE username LIKE :username LIMIT 1")
    fun getUserByUsername(username: String): User
}
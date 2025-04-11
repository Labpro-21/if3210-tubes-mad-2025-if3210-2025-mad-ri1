// SongRepository.kt
package com.example.pertamaxify.data.repository

import com.example.pertamaxify.db.UserDao
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getAllUser() = userDao.getAllUser()

//    suspend fun upsertUser(song: com.example.pertamaxify.data.model.Song) {
//        userDao.(song)
//    }
//
//    suspend fun deleteSong(song: com.example.pertamaxify.data.model.Song) {
//        userDao.deleteSong(song)
//    }
}

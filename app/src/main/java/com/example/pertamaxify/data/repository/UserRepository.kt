// UserRepository.kt
package com.example.pertamaxify.data.repository

import com.example.pertamaxify.data.model.User
import com.example.pertamaxify.db.UserDao
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getAllUser(): List<User> = userDao.getAllUser()

    fun upsertUser(user: User) {
        userDao.upsertUser(user)
    }

    fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    fun getUserByEmail(email: String): User {
        return userDao.getUserByEmail(email)
    }
}

package com.example.pertamaxify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pertamaxify.data.model.LikedSong
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.User
import com.example.pertamaxify.utils.Converters

@Database(
    entities = [Song::class, User::class, LikedSong::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun likedSongDao(): LikedSongDao
    abstract fun playedSongUser(): PlayedSongUserDao
}
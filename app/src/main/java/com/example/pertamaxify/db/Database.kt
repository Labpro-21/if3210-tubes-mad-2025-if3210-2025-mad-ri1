package com.example.pertamaxify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.data.model.Statistic
import com.example.pertamaxify.data.model.User
import com.example.pertamaxify.utils.Converters

@Database(
    entities = [Song::class, User::class, Statistic::class], version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun statisticDao(): StatisticDao
}
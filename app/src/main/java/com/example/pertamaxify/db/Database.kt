package com.example.pertamaxify.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pertamaxify.data.model.Song

@Database(entities = [Song::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: SongDao
}
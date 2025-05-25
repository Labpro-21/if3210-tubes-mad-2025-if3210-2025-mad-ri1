package com.example.pertamaxify.db

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.example.pertamaxify.data.remote.AuthRepository
import com.example.pertamaxify.data.repository.SongRepository
import com.example.pertamaxify.data.repository.StatisticRepository
import com.example.pertamaxify.data.repository.UserRepository
import com.example.pertamaxify.player.MusicPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "pertamaxify_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(database: AppDatabase): SongDao {
        return database.songDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSongRepository(songDao: SongDao): SongRepository {
        return SongRepository(songDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideStatisticDao(database: AppDatabase): StatisticDao {
        return database.statisticDao()
    }

    @Provides
    @Singleton
    fun provideStatisticRepository(statisticDao: StatisticDao): StatisticRepository {
        return StatisticRepository(statisticDao)
    }

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideMusicPlayerManager(
        @ApplicationContext context: Context
    ): MusicPlayerManager {
        return MusicPlayerManager(context)
    }

}

package com.example.pertamaxify.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://34.101.226.132:3000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    val instance: AuthService by lazy {
        createService(AuthService::class.java)
    }

    val songService: SongService by lazy {
        createService(SongService::class.java)
    }

    val profileService: ProfileService by lazy {
        createService(ProfileService::class.java)
    }

    private fun <T> createService(serviceClass: Class<T>): T {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
            .create(serviceClass)
    }
}
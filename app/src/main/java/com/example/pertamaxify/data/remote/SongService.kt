package com.example.pertamaxify.data.remote

import com.example.pertamaxify.data.model.SongResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SongService {
    @GET("top-songs/global")
    suspend fun getGlobalTopSongs(): Response<List<SongResponse>>

    @GET("top-songs/{countryCode}")
    suspend fun getCountryTopSongs(@Path("countryCode") countryCode: String): Response<List<SongResponse>>

    @GET("songs/{id}")
    suspend fun getSongById(@Path("id") id: Int): Response<SongResponse>
}

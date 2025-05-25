package com.example.pertamaxify.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import com.example.pertamaxify.data.model.ProfileUpdateResponse

interface ProfileService {
    @Multipart
    @PATCH("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part location: MultipartBody.Part? = null,
        @Part profilePhoto: MultipartBody.Part? = null
    ): Response<ProfileUpdateResponse> // Replace with your actual response type
}

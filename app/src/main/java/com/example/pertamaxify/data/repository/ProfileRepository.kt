package com.example.pertamaxify.data.repository

import android.util.Log
import okhttp3.MultipartBody
import com.example.pertamaxify.data.remote.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import com.example.pertamaxify.data.model.Result
import com.example.pertamaxify.data.model.ProfileUpdateResponse
import retrofit2.HttpException
import java.io.IOException

@Singleton
class ProfileRepository @Inject constructor() {
    private val profileService = ApiClient.profileService

    /**
     * update User Profile
     */
    suspend fun updateProfile(
        token: String,
        countryCode: String?,
        profilePhoto: File?
    ): Result<ProfileUpdateResponse> {
        return try {
            val locationPart = countryCode?.let {
                MultipartBody.Part.createFormData("location", it)
            }

            val photoPart = profilePhoto?.let { file ->
                MultipartBody.Part.createFormData(
                    "profilePhoto",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }

            val response = profileService.updateProfile(
                token = "Bearer $token",
                location = locationPart,
                profilePhoto = photoPart
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error(IOException("Empty response body"))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("UpdateProfile", "Update failed", e)
            Result.Error(e, "Network request failed")
        }
    }
}

package com.example.pertamaxify.data.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception? = null, val message: String? = null) : Result<Nothing>()
}
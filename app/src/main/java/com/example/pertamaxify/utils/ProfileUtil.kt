package com.example.pertamaxify.utils

import java.util.Locale

fun getCountryNameFromCode(countryCode: String): String {
    return try {
        Locale("", countryCode).displayCountry
    } catch (e: Exception) {
        "Unknown"
    }
}
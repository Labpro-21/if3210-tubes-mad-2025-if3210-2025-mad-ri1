package com.example.pertamaxify.utils

import android.util.Base64
import com.example.pertamaxify.data.model.JwtPayload
import org.json.JSONObject

object JwtUtils {
    fun decodeJwt(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)

            JwtPayload(
                id = jsonObject.getInt("id"),
                username = jsonObject.getString("username"),
                iat = jsonObject.getLong("iat"),
                exp = jsonObject.getLong("exp")
            )
        } catch (e: Exception) {
            null
        }
    }
}

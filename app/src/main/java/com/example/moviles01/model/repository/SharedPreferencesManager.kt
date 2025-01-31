package com.example.moviles01.model.repository

import android.content.Context

class SharedPreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }
}
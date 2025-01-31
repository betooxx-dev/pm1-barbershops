package com.example.moviles01.model.repository

import com.example.moviles01.model.data.LoginResponse
import com.example.moviles01.model.network.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val prefsManager: SharedPreferencesManager
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(mapOf(
                "email" to email.trim(),
                "password" to password.trim()
            ))
            if (response.isSuccessful && response.body() != null) {
                prefsManager.saveToken(response.body()!!.data.token)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de autenticación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(firstName: String, lastName: String, email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.register(mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "password" to password
            ))
            if (response.isSuccessful && response.body() != null) {
                prefsManager.saveToken(response.body()!!.data.token)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        prefsManager.clearToken()
    }
}
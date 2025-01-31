package com.example.moviles01.model.network

import com.example.moviles01.model.repository.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefsManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefsManager.getToken()
        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
                addHeader("Cookie", "token=$it")
            }
        }.build()
        return chain.proceed(request)
    }
}
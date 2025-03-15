package com.example.moviles01.model.network

import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.data.LoginResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body userData: Map<String, String>): Response<LoginResponse>

    @GET("barbershop")
    suspend fun getBarbershops(): Response<List<Barbershop>>

    @POST("barbershop")
    suspend fun createBarbershop(@Body barbershop: Barbershop): Response<Barbershop>

    @PUT("barbershop/{id}")
    suspend fun updateBarbershop(
        @Path("id") id: String,
        @Body barbershop: Barbershop
    ): Response<Barbershop>

    @DELETE("barbershop/{id}")
    suspend fun deleteBarbershop(@Path("id") id: String): Response<Unit>

    @Multipart
    @POST("barbershop/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @POST("auth/fcm-token")
    suspend fun updateFcmToken(@Body tokenData: Map<String, String>): Response<Unit>
}
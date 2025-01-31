package com.example.moviles01

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    suspend fun updateBarbershop(@Path("id") id: String, @Body barbershop: Barbershop): Response<Barbershop>

    @DELETE("barbershop/{id}")
    suspend fun deleteBarbershop(@Path("id") id: String): Response<Unit>
}


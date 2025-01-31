package com.example.moviles01.model.data

data class ResponseData(
    val token: String,
    val user: UserData
)

data class LoginResponse(
    val message: String,
    val data: ResponseData
)
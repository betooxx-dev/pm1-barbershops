package com.example.moviles01

data class UserData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val photo: String
)

data class ResponseData(
    val token: String,
    val user: UserData
)

data class LoginResponse(
    val message: String,
    val data: ResponseData
)
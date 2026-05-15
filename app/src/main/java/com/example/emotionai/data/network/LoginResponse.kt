package com.example.emotionai.data.network

data class LoginResponse(
    val token: String,
    val userId: Int,
    val email: String
)
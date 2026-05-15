package com.example.emotionai.data.network

data class SessionResponse(
    val id: Int,
    val userId: Int,
    val name: String?,
    val startedAt: String,
    val endedAt: String?
)
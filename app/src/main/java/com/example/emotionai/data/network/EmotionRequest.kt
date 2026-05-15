package com.example.emotionai.data.network

data class EmotionRequest(
    val sessionId: Int,
    val label: String,
    val confidence: Float
)
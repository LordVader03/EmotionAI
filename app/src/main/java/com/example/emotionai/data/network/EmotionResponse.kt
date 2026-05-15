package com.example.emotionai.data.network

data class EmotionResponse(
    val id: Int,
    val label: String,
    val confidence: Float,
    val timestamp: String
)
package com.example.emotionai.data

import com.example.emotionai.data.model.EmotionResult
import kotlinx.coroutines.delay

class EmotionRepository {
    suspend fun testBackend(): EmotionResult {
        delay(1200)
        return EmotionResult(
            label = "Happy",
            confidence = 0.87f
        )
    }
}
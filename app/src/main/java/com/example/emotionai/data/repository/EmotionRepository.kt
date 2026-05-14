package com.example.emotionai.data.repository

import android.content.Context
import com.example.emotionai.data.model.EmotionResult
import com.example.emotionai.data.network.EmotionRequest
import com.example.emotionai.data.network.EmotionResponse
import com.example.emotionai.data.network.RetrofitClient
import com.example.emotionai.ml.EmotionInference
import com.example.emotionai.ml.EmotionPreprocessor

class EmotionRepository(context: Context) {

    private val preprocessor = EmotionPreprocessor(context)
    private val inference = EmotionInference(context)
    private val api = RetrofitClient.emotionApi

    fun analyzeFeatures(rawFeatures: FloatArray): EmotionResult {
        val processed = preprocessor.preprocess(rawFeatures)
        val rawConfidence = inference.infer(processed)
        val (label, finalConfidence) = if (rawConfidence >= 0.5f) {
            "Happy" to rawConfidence
        } else {
            "Not Happy" to (1.0f - rawConfidence)
        }
        return EmotionResult(label = label, confidence = finalConfidence)
    }

    suspend fun saveEmotion(sessionId: Int, label: String, confidence: Float): Result<Unit> {
        return try {
            api.createEmotion(EmotionRequest(sessionId, label, confidence))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmotionsBySession(sessionId: Int): Result<List<EmotionResponse>> {
        return try {
            val response = api.getEmotionsBySession(sessionId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkHealth(): Boolean {
        return try {
            api.healthCheck()["status"] == "ok"
        } catch (e: Exception) {
            false
        }
    }

    fun preprocessOnly(rawFeatures: FloatArray): FloatArray {
        return preprocessor.preprocess(rawFeatures)
    }

    fun close() = inference.close()
}
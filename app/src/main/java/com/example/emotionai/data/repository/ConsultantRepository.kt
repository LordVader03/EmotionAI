package com.example.emotionai.data.repository

import com.example.emotionai.data.network.RagRequest
import com.example.emotionai.data.network.RagResponse
import com.example.emotionai.data.network.IngestResponse
import com.example.emotionai.data.network.RetrofitClient

class ConsultantRepository {
    private val api = RetrofitClient.consultantApi

    suspend fun askAssistant(message: String): Result<RagResponse> {
        return try {
            val response = api.askConsultant(RagRequest(message))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ingestEmotionData(summary: String): Result<IngestResponse> {
        return try {
            val response = api.ingestData(RagRequest(summary))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
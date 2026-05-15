package com.example.emotionai.data.network

import retrofit2.http.Body
import retrofit2.http.POST

data class RagRequest(
    val message: String
)

data class RagResponse(
    val answer: String
)

data class IngestResponse(
    val status: String,
    val message: String
)

interface ConsultantApi {
    @POST("/rag/ask")
    suspend fun askConsultant(
        @Body request: RagRequest
    ): RagResponse

    @POST("/rag/ingest")
    suspend fun ingestData(
        @Body request: RagRequest
    ): IngestResponse
}
package com.example.emotionai.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EmotionApi {

    @GET("/health")
    suspend fun healthCheck(): Map<String, String>

    @POST("/api/emotions")
    suspend fun createEmotion(
        @Body request: EmotionRequest
    ): EmotionResponse

    @GET("/api/emotions")
    suspend fun getAllEmotions(): List<EmotionResponse>

    @GET("/api/emotions/{id}")
    suspend fun getEmotion(
        @Path("id") id: Int
    ): EmotionResponse

    @PUT("/api/emotions/{id}")
    suspend fun updateEmotion(
        @Path("id") id: Int,
        @Body request: EmotionRequest
    ): EmotionResponse

    @DELETE("/api/emotions/{id}")
    suspend fun deleteEmotion(
        @Path("id") id: Int
    ): MessageResponse

    @GET("/api/emotions/by-session/{sessionId}")
    suspend fun getEmotionsBySession(
        @Path("sessionId") sessionId: Int
    ): List<EmotionResponse>
}
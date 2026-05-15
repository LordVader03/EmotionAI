package com.example.emotionai.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SessionApi {

    @POST("/api/sessions")
    suspend fun createSession(
        @Body request: CreateSessionRequest
    ): SessionResponse

    @GET("/api/sessions")
    suspend fun getMySessions(): List<SessionResponse>

    @GET("/api/sessions/{id}")
    suspend fun getSessionById(
        @Path("id") id: Int
    ): SessionResponse

    @PUT("/api/sessions/{id}/close")
    suspend fun closeSession(
        @Path("id") id: Int
    ): MessageResponse

    @DELETE("/api/sessions/{id}")
    suspend fun deleteSession(
        @Path("id") id: Int
    ): MessageResponse
}
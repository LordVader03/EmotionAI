package com.example.emotionai.data.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.DELETE

interface AuthApi {
    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): UserResponse

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @DELETE("/api/auth/me")
    suspend fun deleteMyAccount(): MessageResponse
}
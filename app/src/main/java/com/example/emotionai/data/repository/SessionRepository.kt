package com.example.emotionai.data.repository

import com.example.emotionai.data.network.ErrorResponse
import com.example.emotionai.data.network.RetrofitClient
import com.example.emotionai.data.network.CreateSessionRequest
import com.example.emotionai.data.network.SessionResponse
import com.google.gson.Gson
import retrofit2.HttpException

class SessionRepository {

    private val api = RetrofitClient.sessionApi
    private val gson = Gson()

    suspend fun createSession(name: String?): Result<SessionResponse> {
        return try {
            val response = api.createSession(CreateSessionRequest(name))
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun getMySessions(): Result<List<SessionResponse>> {
        return try {
            val response = api.getMySessions()
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun getSessionById(id: Int): Result<SessionResponse> {
        return try {
            val response = api.getSessionById(id)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun closeSession(id: Int): Result<String> {
        return try {
            val response = api.closeSession(id)
            Result.success(response.message)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun deleteSession(id: Int): Result<String> {
        return try {
            val response = api.deleteSession(id)
            Result.success(response.message)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    private fun parseError(e: HttpException): String {
        val errorBody = e.response()?.errorBody()?.string()
        return try {
            gson.fromJson(errorBody, ErrorResponse::class.java)?.message
                ?: "Error del servidor: ${e.code()}"
        } catch (_: Exception) {
            "Error del servidor: ${e.code()}"
        }
    }
}
package com.example.emotionai.data.repository

import com.example.emotionai.data.local.AuthPreferences
import com.example.emotionai.data.network.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AuthRepository(
    private val authPreferences: AuthPreferences
) {
    private val api = RetrofitClient.authApi
    private val gson = Gson()

    val tokenFlow: Flow<String?> = authPreferences.tokenFlow
    val userIdFlow: Flow<Int?> = authPreferences.userIdFlow
    val userEmailFlow: Flow<String?> = authPreferences.userEmailFlow

    suspend fun register(email: String, password: String): Result<UserResponse> {
        return try {
            val response = api.register(RegisterRequest(email, password))
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            authPreferences.saveSession(response.userId, response.email, response.token)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error de red"))
        }
    }

    suspend fun logout() {
        authPreferences.clearSession()
    }

    suspend fun deleteMyAccount(): Result<String> {
        return try {
            val response = api.deleteMyAccount()
            authPreferences.clearSession()
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
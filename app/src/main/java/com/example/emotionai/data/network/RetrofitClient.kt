package com.example.emotionai.data.network

import com.example.emotionai.data.local.AuthPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var authPreferences: AuthPreferences? = null

    fun init(authPreferences: AuthPreferences) {
        this.authPreferences = authPreferences
    }

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()

        val token = runBlocking {
            authPreferences?.getToken()
        }

        val requestBuilder = original.newBuilder()
            .addHeader("Content-Type", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://100.85.9.120:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val sessionApi: SessionApi by lazy { retrofit.create(SessionApi::class.java) }
    val emotionApi: EmotionApi by lazy { retrofit.create(EmotionApi::class.java) }
    val consultantApi: ConsultantApi by lazy { retrofit.create(ConsultantApi::class.java) }
}
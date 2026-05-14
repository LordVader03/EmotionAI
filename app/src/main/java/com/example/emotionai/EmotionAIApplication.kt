package com.example.emotionai

import android.app.Application
import com.example.emotionai.data.local.AuthPreferences
import com.example.emotionai.data.network.RetrofitClient

class EmotionAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val authPreferences = AuthPreferences(this)
        RetrofitClient.init(authPreferences)
    }
}
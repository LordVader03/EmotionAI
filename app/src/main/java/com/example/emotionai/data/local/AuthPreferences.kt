package com.example.emotionai.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val TOKEN = stringPreferencesKey("jwt_token")
    }

    val userIdFlow: Flow<Int?> = context.dataStore.data.map { it[USER_ID] }
    val userEmailFlow: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN] }

    suspend fun saveSession(userId: Int, email: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USER_EMAIL] = email
            prefs[TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun getToken(): String? = tokenFlow.first()
}
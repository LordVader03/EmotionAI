package com.example.emotionai.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "emotionai_session")

class SessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    suspend fun saveSession(token: String, userId: Int, email: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[EMAIL_KEY] = email
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences -> preferences[TOKEN_KEY] }
            .first()
    }

    suspend fun getUserId(): Int? {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences -> preferences[USER_ID_KEY] }
            .first()
    }

    suspend fun getEmail(): String? {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences -> preferences[EMAIL_KEY] }
            .first()
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(EMAIL_KEY)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
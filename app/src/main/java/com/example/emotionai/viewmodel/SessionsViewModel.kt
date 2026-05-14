package com.example.emotionai.viewmodel

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionai.data.network.EmotionResponse
import com.example.emotionai.data.network.SessionResponse
import com.example.emotionai.data.repository.EmotionRepository
import com.example.emotionai.data.repository.SessionRepository
import com.example.emotionai.util.ReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<SessionResponse> = emptyList(),
    val selectedSessionEmotions: List<EmotionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedSessionId: Int? = null,
    val downloadComplete: Boolean = false
)

class SessionsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository()
    private val emotionRepository = EmotionRepository(application)

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = sessionRepository.getMySessions()
            result.onSuccess { sessions ->
                _uiState.update { it.copy(sessions = sessions, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleSessionExpansion(sessionId: Int) {
        val isExpanding = _uiState.value.expandedSessionId != sessionId
        
        if (isExpanding) {
            _uiState.update { it.copy(expandedSessionId = sessionId, selectedSessionEmotions = emptyList()) }
            loadEmotionsForSession(sessionId)
        } else {
            _uiState.update { it.copy(expandedSessionId = null, selectedSessionEmotions = emptyList()) }
        }
    }

    private fun loadEmotionsForSession(sessionId: Int) {
        viewModelScope.launch {
            val result = emotionRepository.getEmotionsBySession(sessionId)
            result.onSuccess { emotions ->
                if (_uiState.value.expandedSessionId == sessionId) {
                    _uiState.update { it.copy(selectedSessionEmotions = emotions) }
                }
            }
        }
    }

    fun exportSessionReport(session: SessionResponse) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, downloadComplete = false) }
            val result = emotionRepository.getEmotionsBySession(session.id)
            result.onSuccess { emotions ->
                val success = savePdfToDownloads(session, emotions)
                if (success) {
                    _uiState.update { it.copy(isLoading = false, downloadComplete = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to save PDF to Downloads") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun savePdfToDownloads(session: SessionResponse, emotions: List<EmotionResponse>): Boolean {
        val fileName = "Session_Report_${session.id}_${System.currentTimeMillis()}.pdf"
        val contentResolver = getApplication<Application>().contentResolver
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
        }

        // Para API < 29 usamos MediaStore.Files, para >= 29 usamos MediaStore.Downloads
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val uri = contentResolver.insert(collection, contentValues)
        return if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    ReportGenerator.generateSessionReport(session, emotions, outputStream)
                } ?: false
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    fun clearDownloadState() {
        _uiState.update { it.copy(downloadComplete = false) }
    }
}
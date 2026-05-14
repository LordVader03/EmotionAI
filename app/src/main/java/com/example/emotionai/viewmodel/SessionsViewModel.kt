package com.example.emotionai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionai.data.network.EmotionResponse
import com.example.emotionai.data.network.SessionResponse
import com.example.emotionai.data.repository.EmotionRepository
import com.example.emotionai.data.repository.SessionRepository
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
    val expandedSessionId: Int? = null
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
}
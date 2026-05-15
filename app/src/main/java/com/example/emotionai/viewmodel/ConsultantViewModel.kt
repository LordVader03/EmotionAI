package com.example.emotionai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionai.data.repository.ConsultantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ConsultantUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ConsultantViewModel : ViewModel() {
    private val repository = ConsultantRepository()

    private val _uiState = MutableStateFlow(ConsultantUiState())
    val uiState: StateFlow<ConsultantUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = Message(text = text, isUser = true)
        _uiState.update { it.copy(
            messages = it.messages + userMessage,
            isLoading = true,
            error = null
        ) }

        viewModelScope.launch {
            val result = repository.askAssistant(text)
            result.onSuccess { response ->
                val assistantMessage = Message(text = response.answer, isUser = false)
                _uiState.update { it.copy(
                    messages = it.messages + assistantMessage,
                    isLoading = false
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Error connecting to AI advisor"
                ) }
            }
        }
    }

    /**
     * Permite enviar datos al RAG para que la IA aprenda de las emociones del usuario.
     */
    fun ingestContext(contextInfo: String) {
        viewModelScope.launch {
            repository.ingestEmotionData(contextInfo)
        }
    }
}
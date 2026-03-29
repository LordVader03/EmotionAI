package com.example.emotionai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionai.data.EmotionRepository
import com.example.emotionai.ui.state.DetectionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetectionViewModel(
    private val repository: EmotionRepository = EmotionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun updatePermissions(cameraGranted: Boolean, audioGranted: Boolean) {
        _uiState.update {
            it.copy(
                hasCameraPermission = cameraGranted,
                hasAudioPermission = audioGranted
            )
        }
    }

    fun toggleCapture() {
        _uiState.update {
            it.copy(isCapturing = !it.isCapturing)
        }
    }

    fun simulateDetection() {
        _uiState.update {
            it.copy(
                currentEmotion = com.example.emotionai.data.model.EmotionResult(
                    label = "Neutral",
                    confidence = 0.75f
                ),
                backendStatus = "Detección simulada local",
                error = null
            )
        }
    }

    fun testBackendConnection() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    backendStatus = "Conectando con backend..."
                )
            }

            try {
                val result = repository.testBackend()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        backendStatus = "Backend conectado correctamente",
                        currentEmotion = result,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        backendStatus = "Error de conexión",
                        error = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}
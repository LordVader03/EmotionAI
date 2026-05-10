package com.example.emotionai.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionai.data.EmotionRepository
import com.example.emotionai.ml.AUExtractor
import com.example.emotionai.ml.FrameAggregator
import com.example.emotionai.ui.state.DetectionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmotionRepository(application)
    private val auExtractor = AUExtractor(application)
    private val aggregator = FrameAggregator()

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun updatePermissions(cameraGranted: Boolean, audioGranted: Boolean) {
        _uiState.update {
            it.copy(hasCameraPermission = cameraGranted, hasAudioPermission = audioGranted)
        }
    }

    fun toggleCapture() {
        val capturing = !_uiState.value.isCapturing
        if (!capturing) aggregator.reset()
        _uiState.update { it.copy(isCapturing = capturing, error = null) }
    }

    /**
     * Llamar en cada frame de CameraX cuando isCapturing == true
     */
    fun processFrame(image: com.google.mediapipe.framework.image.MPImage) {
        if (!_uiState.value.isCapturing) return

        viewModelScope.launch {
            val aus = auExtractor.extractAUs(image) ?: run {
                Log.d("EmotionAI", "No se detectó cara")
                return@launch
            }

            Log.d("EmotionAI", "AUs extraídas: ${aus.take(5).joinToString()}")
            aggregator.addFrame(aus)

            if (aggregator.hasEnoughFrames()) {
                val features = aggregator.computeFeatures() ?: return@launch
                Log.d("EmotionAI", "Features raw (primeras 5): ${features.take(5).joinToString()}")

                val processed = repository.preprocessOnly(features)
                Log.d("EmotionAI", "Features procesadas (primeras 5): ${processed.take(5).joinToString()}")

                val result = repository.analyzeFeatures(features)
                Log.d("EmotionAI", "Resultado: ${result.label} - ${result.confidence}")
                aggregator.reset()

                _uiState.update {
                    it.copy(currentEmotion = result, error = null)
                }
            }
        }
    }

    fun simulateDetection() {
        _uiState.update {
            it.copy(
                currentEmotion = com.example.emotionai.data.model.EmotionResult(
                    label = "Happy", confidence = 0.82f
                ),
                error = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
        auExtractor.close()
    }
}
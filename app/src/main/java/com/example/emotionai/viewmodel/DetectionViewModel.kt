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
    private var isProcessing = false

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun updatePermissions(camera: Boolean, audio: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = camera, hasAudioPermission = audio) }
    }

    fun toggleCapture() {
        _uiState.update { 
            val newState = !it.isCapturing
            if (!newState) aggregator.reset()
            it.copy(isCapturing = newState)
        }
    }

    fun processFrame(image: com.google.mediapipe.framework.image.MPImage) {
        if (!_uiState.value.isCapturing || isProcessing) return

        isProcessing = true
        viewModelScope.launch {
            try {
                val aus = auExtractor.extractAUs(image) ?: run {
                    aggregator.reset()
                    _uiState.update { it.copy(currentEmotion = null) }
                    return@launch
                }

                aggregator.addFrame(aus)

                if (aggregator.hasEnoughFrames()) {
                    val features = aggregator.computeFeatures() ?: return@launch
                    val result = repository.analyzeFeatures(features)
                    aggregator.reset()

                    _uiState.update {
                        it.copy(currentEmotion = result, error = null)
                    }
                }
            } finally {
                isProcessing = false
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
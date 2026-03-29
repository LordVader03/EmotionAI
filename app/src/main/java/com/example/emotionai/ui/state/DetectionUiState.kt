package com.example.emotionai.ui.state

import com.example.emotionai.data.model.EmotionResult

data class DetectionUiState(
    val hasCameraPermission: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val isCapturing: Boolean = false,
    val isLoading: Boolean = false,
    val backendStatus: String? = null,
    val currentEmotion: EmotionResult? = null,
    val error: String? = null
)
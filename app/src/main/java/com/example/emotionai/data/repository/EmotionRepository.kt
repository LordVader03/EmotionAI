package com.example.emotionai.data

import android.content.Context
import com.example.emotionai.data.model.EmotionResult
import com.example.emotionai.ml.EmotionInference
import com.example.emotionai.ml.EmotionPreprocessor

class EmotionRepository(context: Context) {

    private val preprocessor = EmotionPreprocessor(context)
    private val inference = EmotionInference(context)

    fun analyzeFeatures(rawFeatures: FloatArray): EmotionResult {
        val processed = preprocessor.preprocess(rawFeatures)
        val rawConfidence = inference.infer(processed)
        val (label, finalConfidence) = if (rawConfidence >= 0.5f) {
            "Happy" to rawConfidence
        } else {
            "Not Happy" to (1.0f - rawConfidence)
        }
        return EmotionResult(label = label, confidence = finalConfidence)
    }

    fun preprocessOnly(rawFeatures: FloatArray): FloatArray {
        return preprocessor.preprocess(rawFeatures)
    }

    fun close() = inference.close()
}
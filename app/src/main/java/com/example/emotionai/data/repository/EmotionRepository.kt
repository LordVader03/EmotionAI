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
        val confidence = inference.infer(processed)
        val label = if (confidence >= 0.5f) "Happy" else "Not Happy"
        return EmotionResult(label = label, confidence = confidence)
    }

    fun preprocessOnly(rawFeatures: FloatArray): FloatArray {
        return preprocessor.preprocess(rawFeatures)
    }

    fun close() = inference.close()
}
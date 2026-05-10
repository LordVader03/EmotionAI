package com.example.emotionai.ml

import android.content.Context
import org.json.JSONArray

class EmotionPreprocessor(context: Context) {

    private val scalerMean: FloatArray
    private val scalerScale: FloatArray
    private val selectorIndices: IntArray

    init {
        scalerMean = loadJsonFloatArray(context, "best_scaler_mean.json")
        scalerScale = loadJsonFloatArray(context, "best_scaler_scale.json")
        selectorIndices = loadJsonIntArray(context, "best_selector_indices.json")
    }

    private fun loadJsonFloatArray(context: Context, filename: String): FloatArray {
        val json = context.assets.open(filename).bufferedReader().readText()
        val arr = JSONArray(json)
        return FloatArray(arr.length()) { arr.getDouble(it).toFloat() }
    }

    private fun loadJsonIntArray(context: Context, filename: String): IntArray {
        val json = context.assets.open(filename).bufferedReader().readText()
        val arr = JSONArray(json)
        return IntArray(arr.length()) { arr.getInt(it) }
    }

    /**
     * Recibe el vector de 70 features (mean+std de 35 AUs)
     * Devuelve el vector de 50 features listo para TFLite
     */
    fun preprocess(rawFeatures: FloatArray): FloatArray {
        // 1. Normalizar con StandardScaler
        val scaled = FloatArray(rawFeatures.size) { i ->
            (rawFeatures[i] - scalerMean[i]) / scalerScale[i]
        }

        // 2. Seleccionar las 50 mejores features
        return FloatArray(selectorIndices.size) { i ->
            scaled[selectorIndices[i]]
        }
    }
}
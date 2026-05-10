package com.example.emotionai.ml

class FrameAggregator {

    private val auBuffer = mutableListOf<FloatArray>()
    private val AU_COUNT = 35

    fun addFrame(aus: FloatArray) {
        if (aus.size == AU_COUNT) auBuffer.add(aus)
    }

    fun reset() = auBuffer.clear()

    fun hasEnoughFrames() = auBuffer.size >= 10

    /**
     * Calcula mean + std de todos los frames acumulados
     * Devuelve vector de 70 features (igual que el notebook)
     */
    fun computeFeatures(): FloatArray? {
        if (auBuffer.isEmpty()) return null

        val n = auBuffer.size
        val mean = FloatArray(AU_COUNT) { i ->
            auBuffer.sumOf { it[i].toDouble() }.toFloat() / n
        }
        val std = FloatArray(AU_COUNT) { i ->
            val variance = auBuffer.sumOf {
                val diff = (it[i] - mean[i]).toDouble()
                diff * diff
            } / n
            Math.sqrt(variance).toFloat()
        }

        return mean + std
    }

    private operator fun FloatArray.plus(other: FloatArray): FloatArray {
        val result = FloatArray(this.size + other.size)
        this.copyInto(result)
        other.copyInto(result, this.size)
        return result
    }
}
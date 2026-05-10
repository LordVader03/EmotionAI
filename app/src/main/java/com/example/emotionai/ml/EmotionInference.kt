package com.example.emotionai.ml

import android.content.Context
import org.tensorflow.lite.InterpreterApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

class EmotionInference(context: Context) {

    private val interpreter: InterpreterApi

    init {
        val model = loadModelFile(context, "best_tiny_model_OpenFace2.tflite")
        val options = InterpreterApi.Options()
            .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_APPLICATION_ONLY)
        interpreter = InterpreterApi.create(model, options)
    }

    private fun loadModelFile(context: Context, filename: String): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(filename)
        val inputStream = assetFileDescriptor.createInputStream()
        val bytes = inputStream.readBytes()
        inputStream.close()
        val buffer = ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder())
        buffer.put(bytes)
        buffer.rewind()
        return buffer
    }

    fun infer(features: FloatArray): Float {
        val inputBuffer = ByteBuffer
            .allocateDirect(features.size * 4)
            .order(ByteOrder.nativeOrder())
        features.forEach { inputBuffer.putFloat(it) }
        inputBuffer.rewind()

        val outputBuffer = ByteBuffer
            .allocateDirect(4)
            .order(ByteOrder.nativeOrder())

        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        return outputBuffer.float
    }

    fun close() = interpreter.close()
}
package com.example.emotionai.ml

import android.content.Context
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class AUExtractor(context: Context) {

    private val faceLandmarker: FaceLandmarker

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(1)
            .setMinFaceDetectionConfidence(0.3f) // Más sensible para luz baja
            .setMinFacePresenceConfidence(0.3f)
            .setOutputFaceBlendshapes(true)
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }

    fun extractAUs(image: MPImage): FloatArray? {

        val result: FaceLandmarkerResult = faceLandmarker.detect(image)

        if (result.faceBlendshapes().isEmpty) {
            return null
        }

        val bs = result.faceBlendshapes().get()[0]

        fun b(i: Int): Float = bs[i].score()

        fun avg(vararg idx: Int): Float =
            idx.map { b(it) }.average().toFloat()

        fun scale(v: Float, p95: Float): Float {
            return ((v / 0.35f) * p95).coerceIn(0f, 5f)
        }

        fun bin(v: Float, threshold: Float): Float {
            return if (v > threshold) 1f else 0f
        }

        // Intensidades AU_r
        val au1_r  = scale(b(3), 1.62f)
        val au2_r  = scale(avg(4, 5), 0.96f)
        val au4_r  = scale(avg(1, 2), 1.88f)
        val au5_r  = scale(avg(21, 22), 0.56f)
        val au6_r  = scale(avg(7, 8) * 3.0f, 1.20f)
        val au7_r  = scale(avg(19, 20), 1.78f)
        val au9_r  = scale(avg(50, 51), 0.61f)
        val au10_r = scale(avg(48, 49), 2.03f)
        val au12_r = scale(avg(44, 45) * 2.0f, 1.33f)
        val au14_r = scale(avg(28, 29) * 1.5f, 1.53f)
        val au15_r = scale(avg(30, 31), 1.11f)
        val au16_r = scale(avg(34, 35), 2.45f)
        val au17_r = scale(b(42), 0.79f)
        val au18_r = scale(b(38), 1.04f)
        val au20_r = scale(avg(46, 47), 1.99f)
        val au22_r = scale(b(32), 2.07f)
        val au26_r = scale(b(25), 1.28f)

        // Presencia AU_c
        val au1_c  = bin(b(3), 0.15f)
        val au2_c  = bin(avg(4, 5), 0.15f)
        val au4_c  = bin(avg(1, 2), 0.15f)
        val au5_c  = bin(avg(21, 22), 0.10f)
        val au6_c  = bin(avg(7, 8), 0.03f)
        val au7_c  = bin(avg(19, 20), 0.10f)
        val au9_c  = bin(avg(50, 51), 0.05f)
        val au12_c = bin(avg(44, 45), 0.05f)
        val au15_c = bin(avg(30, 31), 0.10f)
        val au17_c = bin(b(42), 0.08f)
        val au18_c = bin(b(38), 0.08f)
        val au20_c = bin(avg(46, 47), 0.12f)
        val au22_c = bin(b(32), 0.10f)
        val au23_c = bin(b(27), 0.10f)
        val au24_c = bin(avg(36, 37), 0.10f)
        val au26_c = bin(b(25), 0.08f)
        val au45_c = bin(avg(9, 10), 0.15f)
        val au33_c = bin(b(6), 0.10f)

        return floatArrayOf(
            au1_r, au2_r, au4_r, au5_r, au6_r, au7_r, au9_r, au10_r, au12_r, au14_r, au15_r, au16_r, au17_r, au18_r, au20_r, au22_r, au26_r,
            au1_c, au2_c, au4_c, au5_c, au6_c, au7_c, au9_c, au12_c, au15_c, au17_c, au18_c, au20_c, au22_c, au23_c, au24_c, au26_c, au45_c, au33_c
        )
    }

    fun close() {
        faceLandmarker.close()
    }
}

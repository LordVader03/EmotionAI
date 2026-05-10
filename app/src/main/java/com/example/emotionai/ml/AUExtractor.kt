package com.example.emotionai.ml

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.framework.image.MPImage

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
            .setOutputFaceBlendshapes(true)  // blendshapes ≈ AUs
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }

    /**
     * Extrae el vector de 35 AUs de un frame.
     * Devuelve null si no se detecta cara.
     */
    fun extractAUs(image: MPImage): FloatArray? {
        val result: FaceLandmarkerResult = faceLandmarker.detect(image)
        if (result.faceBlendshapes().isEmpty) return null

        val blendshapes = result.faceBlendshapes().get()[0]

        // MediaPipe tiene 52 blendshapes. Seleccionamos los 35
        // más equivalentes a las AUs de OpenFace2.
        // Intensidad (17): indices 0-16
        // Presencia/binario (18): indices 17-34
        val auIndices = intArrayOf(
            // Intensidad (~AU_r): browDown, browInnerUp, browOuterUp,
            // cheekPuff, cheekSquint, eyeBlink, eyeLookDown, eyeLookIn,
            // eyeLookOut, eyeLookUp, eyeSquint, eyeWide,
            // jawOpen, mouthClose, mouthFunnel, mouthSmile, noseSneer
            7, 4, 5, 13, 14, 9, 10, 11, 12, 16, 17, 18,
            25, 26, 27, 44, 49,
            // Presencia (~AU_c): resto relevantes
            0, 1, 2, 3, 6, 8, 15, 19, 20, 21, 22, 23,
            24, 28, 29, 30, 31, 32
        )

        return FloatArray(35) { i ->
            blendshapes[auIndices[i]].score()
        }
    }

    fun close() = faceLandmarker.close()
}
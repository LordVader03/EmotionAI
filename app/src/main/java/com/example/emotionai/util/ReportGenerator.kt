package com.example.emotionai.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.pdf.PdfDocument
import com.example.emotionai.data.network.EmotionResponse
import com.example.emotionai.data.network.SessionResponse
import java.io.OutputStream

object ReportGenerator {

    fun generateSessionReport(
        session: SessionResponse,
        emotions: List<EmotionResponse>,
        outputStream: OutputStream
    ): Boolean {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }

        // Header
        canvas.drawText("EMOTION AI - SESSION REPORT", 50f, 50f, titlePaint)
        canvas.drawText("Session: ${session.name ?: "Unnamed"}", 50f, 80f, textPaint)
        canvas.drawText("Date: ${session.startedAt}", 50f, 100f, textPaint)
        canvas.drawText("Total Detections: ${emotions.size}", 50f, 120f, textPaint)

        // Draw Graph
        if (emotions.isNotEmpty()) {
            drawHappinessGraph(canvas, emotions, 100f, 150f, 440f, 300f)
        } else {
            canvas.drawText("No data available for graph", 100f, 200f, textPaint)
        }

        pdfDocument.finishPage(page)

        return try {
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            true
        } catch (e: Exception) {
            pdfDocument.close()
            false
        }
    }

    private fun drawHappinessGraph(
        canvas: Canvas,
        emotions: List<EmotionResponse>,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val gridPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val linePaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        val areaPaint = Paint().apply {
            color = Color.argb(40, 33, 150, 243)
            style = Paint.Style.FILL
        }

        // Draw borders
        canvas.drawRect(x, y, x + width, y + height, borderPaint)
        
        // Draw grid (25%, 50%, 75%)
        for (i in 1..3) {
            val gy = y + (height * i / 4f)
            canvas.drawLine(x, gy, x + width, gy, gridPaint)
        }

        if (emotions.size < 2) return

        val happinessPoints = emotions.map {
            if (it.label == "Happy") it.confidence else 1f - it.confidence
        }

        val stepX = width / (happinessPoints.size - 1)
        val path = Path()
        val areaPath = Path()

        happinessPoints.forEachIndexed { index, happiness ->
            val px = x + (index * stepX)
            val py = y + height - (happiness * height)
            
            if (index == 0) {
                path.moveTo(px, py)
                areaPath.moveTo(px, y + height)
                areaPath.lineTo(px, py)
            } else {
                path.lineTo(px, py)
                areaPath.lineTo(px, py)
            }
            
            if (index == happinessPoints.size - 1) {
                areaPath.lineTo(px, y + height)
                areaPath.close()
            }
        }

        canvas.drawPath(areaPath, areaPaint)
        canvas.drawPath(path, linePaint)
        
        // Labels
        val labelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("100% Happiness", x - 10f, y + 4f, labelPaint)
        canvas.drawText("75%", x - 10f, y + (height * 0.25f) + 4f, labelPaint)
        canvas.drawText("50%", x - 10f, y + (height * 0.50f) + 4f, labelPaint)
        canvas.drawText("25%", x - 10f, y + (height * 0.75f) + 4f, labelPaint)
        canvas.drawText("0%", x - 10f, y + height + 4f, labelPaint)
    }
}
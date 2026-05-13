package com.example.emotionai.ui.detection

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.emotionai.viewmodel.DetectionViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsState()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updatePermissions(granted, state.hasAudioPermission)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.updatePermissions(granted, true)
        if (!granted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EMOTION AI SCANNER", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleCapture() },
                containerColor = if (state.isCapturing) Color(0xFFFF5252) else Color(0xFF2196F3),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (state.isCapturing) Icons.Default.Stop else Icons.Default.CameraAlt,
                    contentDescription = "Capturar",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            
            // 1. CÁMARA (FIT_CENTER para ver TODO el sensor)
            if (state.hasCameraPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp) // Espacio para el botón
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FIT_CENTER // << CLAVE: Sin recortes
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val imageAnalyzer = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .setTargetResolution(android.util.Size(720, 1280))
                                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                    .build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                            if (state.isCapturing) {
                                                try {
                                                    // MediaPipe detect() prefiere MPImage desde Bitmap o Media Image
                                                    // Usamos el bitmap directo para asegurar consistencia de color/luz
                                                    val bitmap = imageProxy.toBitmap()
                                                    val mpImage = BitmapImageBuilder(bitmap).build()
                                                    viewModel.processFrame(mpImage)
                                                } catch (e: Exception) {
                                                    Log.e("DetectionScreen", "Error", e)
                                                }
                                            }
                                            imageProxy.close()
                                        }
                                    }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_FRONT_CAMERA,
                                    preview,
                                    imageAnalyzer
                                )
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay de Guía (El óvalo para el rostro)
                    FaceGuideOverlay(state.isCapturing)
                }
            }

            // 2. Resultado Flotante
            AnimatedVisibility(
                visible = state.isCapturing && state.currentEmotion != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp, start = 32.dp, end = 32.dp)
            ) {
                ResultOverlay(state.currentEmotion)
            }
        }
    }
}

@Composable
fun FaceGuideOverlay(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scanLinePos by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "line"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer()
    ) {
        val width = size.width
        val height = size.height
        val rectWidth = width * 0.75f
        val rectHeight = rectWidth * 1.3f
        val left = (width - rectWidth) / 2
        val top = (height - rectHeight) / 2

        // Marco técnico (Esquinas)
        val cornerLen = 40.dp.toPx()
        val strokeW = 2.dp.toPx()
        val color = if (isScanning) Color(0xFF2196F3) else Color.White.copy(alpha = 0.2f)

        // Dibujar solo las esquinas para un look más "limpio" y militar
        // Top Left
        drawLine(color, Offset(left, top), Offset(left + cornerLen, top), strokeW)
        drawLine(color, Offset(left, top), Offset(left, top + cornerLen), strokeW)
        // Top Right
        drawLine(color, Offset(left + rectWidth - cornerLen, top), Offset(left + rectWidth, top), strokeW)
        drawLine(color, Offset(left + rectWidth, top), Offset(left + rectWidth, top + cornerLen), strokeW)
        // Bottom Left
        drawLine(color, Offset(left, top + rectHeight), Offset(left + cornerLen, top + rectHeight), strokeW)
        drawLine(color, Offset(left, top + rectHeight - cornerLen), Offset(left, top + rectHeight), strokeW)
        // Bottom Right
        drawLine(color, Offset(left + rectWidth - cornerLen, top + rectHeight), Offset(left + rectWidth, top + rectHeight), strokeW)
        drawLine(color, Offset(left + rectWidth, top + rectHeight - cornerLen), Offset(left + rectWidth, top + rectHeight), strokeW)

        // Línea de escaneo
        if (isScanning) {
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0xFF2196F3).copy(alpha = 0.6f), Color.Transparent)
                ),
                start = Offset(left + 10.dp.toPx(), top + (rectHeight * scanLinePos)),
                end = Offset(left + rectWidth - 10.dp.toPx(), top + (rectHeight * scanLinePos)),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun ResultOverlay(emotion: com.example.emotionai.data.model.EmotionResult?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
        color = Color(0xFF121212).copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            emotion?.let {
                val color = if (it.label == "Happy") Color(0xFF4CAF50) else Color(0xFFFFA000)
                Text(
                    text = it.label.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = color
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { it.confidence },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = color,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    text = "${(it.confidence * 100).toInt()}% CONFIDENCE",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

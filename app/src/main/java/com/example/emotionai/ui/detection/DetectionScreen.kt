package com.example.emotionai.ui.detection

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.emotionai.viewmodel.DetectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        viewModel.updatePermissions(cameraGranted, audioGranted)
    }

    LaunchedEffect(Unit) {
        val cameraGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val audioGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.updatePermissions(cameraGranted, audioGranted)

        if (!cameraGranted || !audioGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detección") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Permisos cámara: ${state.hasCameraPermission}")
            Text("Permisos audio: ${state.hasAudioPermission}")

            Button(onClick = { viewModel.toggleCapture() }) {
                Text(if (state.isCapturing) "Parar captura" else "Iniciar captura")
            }

            Button(onClick = { viewModel.simulateDetection() }) {
                Text("Simular detección")
            }

            state.currentEmotion?.let {
                Text("Emoción: ${it.label} (${(it.confidence * 100).toInt()}%)")
            }

            Button(
                onClick = { viewModel.testBackendConnection() },
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Conectando..." else "Probar backend")
            }

            state.backendStatus?.let {
                Text("Estado backend: $it")
            }

            state.error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
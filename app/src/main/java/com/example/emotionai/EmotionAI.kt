package com.example.emotionai

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emotionai.core.navigation.NavRoutes
import com.example.emotionai.ui.home.HomeScreen
import com.example.emotionai.ui.detection.DetectionScreen
import com.example.emotionai.ui.settings.SettingsScreen
import com.example.emotionai.viewmodel.HomeViewModel
import com.example.emotionai.viewmodel.DetectionViewModel
import com.example.emotionai.viewmodel.SettingsViewModel

@Composable
fun EmotionAI() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            val vm: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = vm,
                onNavigateToDetection = { navController.navigate(NavRoutes.DETECTION) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }
        composable(NavRoutes.DETECTION) {
            val vm: DetectionViewModel = viewModel()
            DetectionScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.SETTINGS) {
            val vm: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
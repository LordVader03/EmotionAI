package com.example.emotionai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emotionai.core.navigation.NavRoutes
import com.example.emotionai.ui.auth.LoginScreen
import com.example.emotionai.ui.auth.RegisterScreen
import com.example.emotionai.ui.detection.DetectionScreen
import com.example.emotionai.ui.home.HomeScreen
import com.example.emotionai.ui.sessions.SessionsScreen
import com.example.emotionai.ui.settings.SettingsScreen
import com.example.emotionai.viewmodel.AuthViewModel
import com.example.emotionai.viewmodel.DetectionViewModel
import com.example.emotionai.viewmodel.HomeViewModel
import com.example.emotionai.viewmodel.SessionsViewModel
import com.example.emotionai.viewmodel.SettingsViewModel

@Composable
fun EmotionAI() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    // Usamos un valor sentinela para el estado inicial de carga
    val token by authViewModel.tokenFlow.collectAsState(initial = "INITIAL_LOADING")

    LaunchedEffect(token) {
        if (token == null) {
            authViewModel.resetState()
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (token == "INITIAL_LOADING") return

    NavHost(
        navController = navController,
        startDestination = if (token == null) NavRoutes.LOGIN else NavRoutes.HOME
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.HOME) {
            val vm: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = vm,
                onNavigateToDetection = { navController.navigate(NavRoutes.DETECTION) },
                onNavigateToSessions = { navController.navigate(NavRoutes.SESSIONS) },
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
        composable(NavRoutes.SESSIONS) {
            val vm: SessionsViewModel = viewModel()
            SessionsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.SETTINGS) {
            val vm: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = vm,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
package com.example.emotionai.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.unit.sp
import com.example.emotionai.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetection: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Background Decorative Gradients
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-150).dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2196F3).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo/Header
            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "EMOTION AI",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    color = Color.White
                )
            )
            
            Text(
                text = "NEURAL ANALYSIS SYSTEM",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    color = Color(0xFF2196F3).copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Menu Cards
            MenuCard(
                title = "REAL-TIME DETECTION",
                subtitle = "Analyze facial expressions via camera",
                icon = Icons.Default.Face,
                color = Color(0xFF2196F3),
                onClick = onNavigateToDetection
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "MISSION HISTORY",
                subtitle = "Review past sessions and emotions",
                icon = Icons.Default.History,
                color = Color(0xFF00BCD4),
                onClick = onNavigateToSessions
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                title = "SETTINGS",
                subtitle = "Configure neural parameters",
                icon = Icons.Default.Settings,
                color = Color.White.copy(alpha = 0.7f),
                onClick = onNavigateToSettings
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "V 1.0.4 - SECURE CORE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

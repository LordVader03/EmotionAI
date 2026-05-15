package com.example.emotionai.ui.consultant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionai.viewmodel.ConsultantViewModel
import com.example.emotionai.viewmodel.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantScreen(
    viewModel: ConsultantViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var textFieldValue by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "AI EMOTIONAL ADVISOR", 
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.messages) { message ->
                    ChatBubble(message)
                }
                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(8.dp),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    placeholder = { Text("Ask about your emotions...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(textFieldValue)
                        textFieldValue = ""
                    },
                    enabled = textFieldValue.isNotBlank() && !state.isLoading
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textFieldValue.isNotBlank()) Color(0xFF2196F3) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val color = if (message.isUser) Color(0xFF2196F3) else Color(0xFF1E1E1E)
    val textColor = Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = color,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun PaymentProcessingScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2500) // Wait 2.5 seconds
        navController.navigate("success") {
            popUpTo("home")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F6F8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFF60B246),
                strokeWidth = 5.dp,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Processing Payment...", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please do not press back or close the app.", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
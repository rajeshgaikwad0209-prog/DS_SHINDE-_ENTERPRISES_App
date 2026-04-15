package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, cartViewModel: CartViewModel) {
    val amountToPay = cartViewModel.totalPrice + 45 // Including delivery & platform fee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Amount to Pay: ₹$amountToPay", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF60B246))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ALL THREE OPTIONS NOW GO TO THE LOADING SCREEN
            PaymentOptionCard("UPI (Google Pay, PhonePe)", "Pay instantly via UPI") {
                navigateToProcessing(navController)
            }
            PaymentOptionCard("Credit / Debit Card", "Visa, MasterCard, RuPay") {
                navigateToProcessing(navController)
            }
            PaymentOptionCard("Cash on Delivery (COD)", "Pay at your doorstep") {
                navigateToProcessing(navController)
            }
        }
    }
}

@Composable
fun PaymentOptionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

// The updated helper function that routes to "processing"
fun navigateToProcessing(navController: NavController) {
    navController.navigate("processing") {
        popUpTo("payment") { inclusive = true }
    }
}
package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderSuccessScreen(navController: NavController, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }

    LaunchedEffect(Unit) {
        // Only save an order if there are actually items in the cart
        if (cartViewModel.totalItems > 0) {

            // 1. Combine all food names into a single string (e.g., "Biryani, Garlic Naan")
            val itemsOrdered = cartViewModel.cartItems.keys.joinToString(", ") { it.name }

            // 2. Calculate grand total (Items + 45 Delivery/Platform fee)
            val grandTotal = "₹${cartViewModel.totalPrice + 45}"

            // 3. Get the exact exact current time and format it
            val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            val dateString = "Ordered on ${formatter.format(Date())}"

            // 4. Save it dynamically to the phone!
            authManager.saveOrder("FeastFlow Order", itemsOrdered, grandTotal, dateString)

            // 5. Finally, clear the cart for the next order
            cartViewModel.clearCart()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF60B246)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = Color.White) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color(0xFF60B246),
                    modifier = Modifier.padding(16.dp).size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Payment Successful!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your food is being prepared.", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { navController.navigate("home") { popUpTo(0) } },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).padding(horizontal = 32.dp)
            ) {
                Text("Track Order / Go Home", color = Color(0xFF60B246), fontWeight = FontWeight.Bold)
            }
        }
    }
}
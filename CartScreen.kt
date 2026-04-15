package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartItems = cartViewModel.cartItems // This is a Map<MenuItem, Int>
    val itemTotal = cartViewModel.totalPrice
    val deliveryFee = 40
    val platformFee = 5
    val grandTotal = itemTotal + deliveryFee + platformFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Only show the pay button if there are items in the cart
            if (cartViewModel.totalItems > 0) {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigate("payment") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60B246)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Proceed to Pay  •  ₹$grandTotal",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        if (cartViewModel.totalItems == 0) {
            // Empty Cart View
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is empty", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Looks like you haven't added anything yet.", color = Color.Gray)
                }
            }
        } else {
            // Filled Cart View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F6F8)) // Light grey background
            ) {
                // 1. ITEMS LIST
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("The Bombay Canteen", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text("Lower Parel", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFF5F6F8), thickness = 2.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Loop through the Map of items in the cart
                            cartItems.forEach { (item, quantity) ->
                                CartItemRow(item, quantity, cartViewModel)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                // 2. BILL DETAILS
                item {
                    Text(
                        text = "Bill Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BillRow("Item Total", "₹$itemTotal")
                            Spacer(modifier = Modifier.height(8.dp))
                            BillRow("Delivery Fee", "₹$deliveryFee")
                            Spacer(modifier = Modifier.height(8.dp))
                            BillRow("Platform Fee", "₹$platformFee")
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("To Pay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                Text("₹$grandTotal", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Bottom padding to ensure you can scroll past the bottom button
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

// Re-usable row for the food items in the cart
@Composable
fun CartItemRow(item: MenuItem, quantity: Int, cartViewModel: CartViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Veg Icon & Name
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            val iconColor = if (item.isVeg) Color(0xFF0F8A65) else Color(0xFFE15141)
            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(iconColor))
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }

        // Quantity Toggle Logic
        Row(
            modifier = Modifier
                .width(80.dp)
                .height(32.dp)
                .background(Color(0xFFFBE9E7), RoundedCornerShape(8.dp)) // Light orange background
                .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { cartViewModel.removeItem(item) }) {
                Text("-", color = Color(0xFFFC8019), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Text(quantity.toString(), color = Color(0xFFFC8019), fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { cartViewModel.addItem(item) }) {
                Text("+", color = Color(0xFFFC8019), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
        }

        // Total price for that specific item
        Text(
            text = "₹${item.price * quantity}",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp).padding(start = 8.dp)
        )
    }
}

// Re-usable row for the bill details
@Composable
fun BillRow(title: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = Color.Gray, fontSize = 14.sp)
        Text(amount, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
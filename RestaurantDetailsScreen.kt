package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreen(navController: NavController, cartViewModel: CartViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F6F8))) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("The Bombay Canteen", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                        Text("North Indian, Biryani • Lower Parel", color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray)
                        Spacer(Modifier.height(12.dp))
                        Text("🏍️ 30-35 mins  •  ₹400 for two", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Text("Recommended", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(16.dp))
            }

            items(getMockMenu()) { item ->
                val quantity = cartViewModel.getQuantity(item)
                Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("₹${item.price}", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(item.description, color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(0.4f).height(130.dp), contentAlignment = Alignment.BottomCenter) {
                        AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(110.dp).align(Alignment.TopCenter).clip(RoundedCornerShape(12.dp)))

                        if (quantity == 0) {
                            Button(
                                onClick = { cartViewModel.addItem(item) },
                                modifier = Modifier.width(100.dp).height(40.dp).offset(y = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                                shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)
                            ) { Text("ADD", color = Color(0xFF1DB346), fontWeight = FontWeight.ExtraBold) }
                        } else {
                            Row(
                                modifier = Modifier.width(100.dp).height(40.dp).offset(y = 8.dp).clip(RoundedCornerShape(8.dp)).background(Color.White),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(onClick = { cartViewModel.removeItem(item) }) { Text("-", color = Color(0xFF1DB346), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) }
                                Text(quantity.toString(), color = Color(0xFF1DB346), fontWeight = FontWeight.ExtraBold)
                                IconButton(onClick = { cartViewModel.addItem(item) }) { Text("+", color = Color(0xFF1DB346), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) }
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
            item { Spacer(Modifier.height(100.dp)) } // Padding for cart banner
        }
    }
}
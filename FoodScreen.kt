package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun FoodScreen(navController: NavController, cartViewModel: CartViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val allMenu = getMockMenu() // Grabs the food items from Models.kt

    // Filter logic for the search bar
    val filteredMenu = if (searchQuery.isEmpty()) {
        allMenu
    } else {
        allMenu.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Food", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp) },
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
            // Reusing the SearchBar we already built in HomeScreen.kt!
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            Spacer(modifier = Modifier.height(16.dp))

            // THE 2-COLUMN GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Forces exactly 2 columns
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp), // Bottom padding for cart banner
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredMenu) { item ->
                    FoodGridCard(item = item, cartViewModel = cartViewModel)
                }
            }
        }
    }
}

// A custom card designed specifically for the Grid layout
@Composable
fun FoodGridCard(item: MenuItem, cartViewModel: CartViewModel) {
    val quantity = cartViewModel.getQuantity(item)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Food Image at the top
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            // Details Below
            Column(modifier = Modifier.padding(12.dp)) {
                // Veg/Non-Veg icon
                val iconColor = if (item.isVeg) Color(0xFF0F8A65) else Color(0xFFE15141)
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(iconColor))
                Spacer(modifier = Modifier.height(4.dp))

                // Name & Price
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text("₹${item.price}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // The Smart Add Button Logic
                if (quantity == 0) {
                    Button(
                        onClick = { cartViewModel.addItem(item) },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBE9E7)), // Light orange
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("ADD", color = Color(0xFFFC8019), fontWeight = FontWeight.ExtraBold)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Color(0xFFFBE9E7), RoundedCornerShape(8.dp))
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
                }
            }
        }
    }
}
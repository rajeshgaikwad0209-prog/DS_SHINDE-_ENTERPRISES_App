package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
fun InstamartScreen(navController: NavController, cartViewModel: CartViewModel) {
    // 1. STATE FOR CATEGORY FILTERING
    var selectedCategory by remember { mutableStateOf("All") }

    // 2. MOCK GROCERY DATA (Reusing MenuItem so the Cart works instantly!)
    val allGroceries = listOf(
        MenuItem("101", "Lay's India's Magic Masala", 20, "Snacks", true, "https://www.bbassets.com/media/uploads/p/l/294281_13-lays-potato-chips-indias-magic-masala.jpg"),
        MenuItem("102", "Amul Taaza Toned Milk", 30, "Dairy", true, "https://www.bbassets.com/media/uploads/p/l/40090894_7-amul-taaza.jpg"),
        MenuItem("103", "Farm Fresh Bananas (500g)", 45, "Fruits & Veggies", true, "https://tiimg.tistatic.com/fp/1/007/682/rich-in-vitamin-c-healthy-indian-origin-naturally-grown-farm-fresh-yellow-banana--576.jpg"),
        MenuItem("104", "Coca-Cola Can (300ml)", 40, "Beverages", true, "https://www.grosta.in/public/storage/app/public/images/products/large/130.jpg"),
        MenuItem("105", "Farm Eggs (Pack of 6)", 60, "Dairy", false, "https://www.bbassets.com/media/uploads/p/xxl/281204_9-fresho-farm-eggs-regular-medium-antibiotic-residue-free.jpg")
    )

    // Filter logic
    val filteredGroceries = if (selectedCategory == "All") {
        allGroceries
    } else {
        allGroceries.filter { it.description == selectedCategory } // We use description to store the category
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Instamart", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFFD11243)) // Instamart Pink/Red
                        Text("Delivery in 10 minutes", color = Color.Gray, fontSize = 12.sp)
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
            // 3. HORIZONTAL CATEGORY SELECTOR
            val categories = listOf("All", "Snacks", "Dairy", "Fruits & Veggies", "Beverages")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFFD11243) else Color.White)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // 4. THE GROCERY GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredGroceries) { item ->
                    GroceryCard(item = item, cartViewModel = cartViewModel)
                }
            }
        }
    }
}

@Composable
fun GroceryCard(item: MenuItem, cartViewModel: CartViewModel) {
    val quantity = cartViewModel.getQuantity(item)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Grocery Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Fit, // Fit looks better for packaged goods
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F6F8)) // Slight background for transparent PNGs
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Name & Price
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2, modifier = Modifier.height(40.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("₹${item.price}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Instamart-styled Add Button (Using Pink/Red instead of Green)
            if (quantity == 0) {
                Button(
                    onClick = { cartViewModel.addItem(item) },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD11243)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("ADD", color = Color(0xFFD11243), fontWeight = FontWeight.ExtraBold)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(Color(0xFFD11243), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { cartViewModel.removeItem(item) }) {
                        Text("-", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.ExtraBold)
                    IconButton(onClick = { cartViewModel.addItem(item) }) {
                        Text("+", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
package com.example.feastflow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Added missing import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // --- THIS WAS THE MISSING PIECE ---
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    // ----------------------------------

    // 1. ALL OUR STATES
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // NEW STATES FOR LOCATION
    var currentLocation by remember { mutableStateOf(authManager.getLocation()) }
    var showLocationSheet by remember { mutableStateOf(false) }

    val allRestaurants = getMockRestaurants()
    val filteredRestaurants = allRestaurants.filter { restaurant ->
        val matchesSearch = if (searchQuery.isEmpty()) true else {
            restaurant.name.contains(searchQuery, ignoreCase = true) ||
                    restaurant.cuisines.contains(searchQuery, ignoreCase = true)
        }
        val matchesCategory = if (selectedCategory == null) true else {
            restaurant.cuisines.contains(selectedCategory!!, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .statusBarsPadding()
        ) {
            // 2. PASS CLICKS TO THE HEADER
            item {
                LocationHeader(
                    locationText = currentLocation,
                    onLocationClick = { showLocationSheet = true },
                    onProfileClick = { navController.navigate("account") } // Jumps to Account tab
                )
            }

            item { SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }) }

            item {
                CategorySection(
                    selectedCategory = selectedCategory,
                    onCategoryClick = { clickedCategory ->
                        selectedCategory = if (selectedCategory == clickedCategory) null else clickedCategory
                    }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text("Restaurants to explore", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }

            if (filteredRestaurants.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No restaurants found 😕", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                items(filteredRestaurants) { restaurant ->
                    RestaurantCard(restaurant, onClick = { navController.navigate("restaurant/${restaurant.id}") })
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }

    // 3. THE BOTTOM SHEET LOGIC
    if (showLocationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLocationSheet = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select a Location", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // A mock list of locations to pick from
                val locations = listOf(
                    "Andheri West, Mumbai",
                    "Bandra Kurla Complex, Mumbai",
                    "Powai, Mumbai",
                    "Juhu, Mumbai"
                )

                locations.forEach { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentLocation = location
                                authManager.saveLocation(location) // Save globally
                                showLocationSheet = false
                            }
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(location, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = Color(0xFFF5F6F8))
                }
                Spacer(modifier = Modifier.height(32.dp)) // Padding for the bottom
            }
        }
    }
}

// 4. UPDATE HEADER TO ACCEPT CLICKS AND DYNAMIC TEXT
@Composable
fun LocationHeader(
    locationText: String,
    onLocationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Location (Now clickable)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onLocationClick() }
                .padding(end = 16.dp) // Gives a bit of tap target padding
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFC8019))
            Spacer(Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null) // Added dropdown arrow
                }
                Text(locationText, color = Color.Gray, fontSize = 12.sp)
            }
        }

        // Right side: Profile Icon (Now clickable)
        Icon(
            Icons.Default.Person,
            contentDescription = "Account",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() }
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query, onValueChange = onQueryChange, placeholder = { Text("Search for 'Biryani' or 'Burger'", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = { if (query.isNotEmpty()) { IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.Gray) } } },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
        shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, focusedBorderColor = Color(0xFFFC8019))
    )
}

@Composable
fun CategorySection(selectedCategory: String?, onCategoryClick: (String) -> Unit) {
    Spacer(Modifier.height(24.dp))
    Text("What's on your mind?", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
    Spacer(Modifier.height(12.dp))
    val categories = listOf("Pizza" to "🍕", "Burger" to "🍔", "Biryani" to "🥘", "Healthy" to "🥗")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(categories) { (name, emoji) ->
            val isSelected = selectedCategory == name
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(76.dp).clip(CircleShape).background(if (isSelected) Color(0xFFFC8019).copy(alpha = 0.1f) else Color(0xFFF5F6F8)).border(width = if (isSelected) 2.dp else 0.dp, color = if (isSelected) Color(0xFFFC8019) else Color.Transparent, shape = CircleShape).clickable { onCategoryClick(name) },
                    contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 32.sp) }
                Spacer(Modifier.height(8.dp))
                Text(text = name, fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium, color = if (isSelected) Color(0xFFFC8019) else Color.Black, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp)) {
        AsyncImage(model = restaurant.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF24963F), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${restaurant.rating} • ${restaurant.deliveryTime}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text(restaurant.cuisines, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
package com.example.feastflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun FeastFlowApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val cartViewModel: CartViewModel = viewModel()

    // 1. CHECK IF USER IS ALREADY LOGGED IN
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val startScreen = if (authManager.isLoggedIn()) "home" else "login" // Dynamic start

    // 2. Define which screens SHOULD have the bottom bar and cart banner
    val screensWithBottomNav = listOf("home", "food", "instamart", "account")
    val isMainAppScreen = currentRoute in screensWithBottomNav

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // We hide the bottom bar AND the cart completely if we are on Login/Register
            if (currentRoute != "login" && currentRoute != "register") {
                Column {
                    // Cart Banner
                    AnimatedVisibility(
                        visible = cartViewModel.totalItems > 0,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF60B246))
                                .clickable { navController.navigate("cart") }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${cartViewModel.totalItems} ITEMS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("₹${cartViewModel.totalPrice}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("View Cart", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    // Bottom Navigation
                    if (isMainAppScreen) {
                        val tabs = listOf(
                            Triple("home", "FeastFlow", Icons.Default.Home),
                            Triple("food", "Food", Icons.AutoMirrored.Filled.List),
                            Triple("instamart", "Instamart", Icons.Default.ShoppingCart),
                            Triple("account", "Account", Icons.Default.Person)
                        )
                        NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                            tabs.forEach { (route, label, icon) ->
                                val isSelected = currentRoute == route
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (!isSelected) navController.navigate(route) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(icon, contentDescription = label, tint = if (isSelected) Color(0xFFFC8019) else Color.Gray) },
                                    label = { Text(label, color = if (isSelected) Color.Black else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startScreen, // 3. USE DYNAMIC START SCREEN HERE
            modifier = Modifier.padding(innerPadding)
        ) {
            // AUTH ROUTES
            composable("login") { LoginScreen(navController) }
            composable("register") { RegistrationScreen(navController) }

            // MAIN APP ROUTES
            composable("home") { HomeScreen(navController) }
            composable("food") { FoodScreen(navController, cartViewModel) }
            composable("instamart") { InstamartScreen(navController, cartViewModel) }
            composable("account") { AccountScreen(navController) }

            composable("restaurant/{id}") { RestaurantDetailsScreen(navController, cartViewModel) }

            // CHECKOUT FLOW ROUTES
            composable("cart") { CartScreen(navController, cartViewModel) }
            composable("payment") { PaymentScreen(navController, cartViewModel) }
            composable("processing") { PaymentProcessingScreen(navController) }
            composable("success") { OrderSuccessScreen(navController, cartViewModel) }
        }
    }
}
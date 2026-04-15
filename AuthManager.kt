package com.example.feastflow

import android.content.Context
import android.content.SharedPreferences

// 1. Define what an order looks like here so the whole app can use it
data class PastOrder(val restaurantName: String, val items: String, val price: String, val date: String)

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FeastFlowPrefs", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean("IS_LOGGED_IN", false)

    fun registerUser(name: String, email: String, pass: String) {
        prefs.edit()
            .putString("SAVED_NAME", name)
            .putString("SAVED_EMAIL", email)
            .putString("SAVED_PASS", pass)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }

    fun loginUser(email: String, pass: String): Boolean {
        val savedEmail = prefs.getString("SAVED_EMAIL", "")
        val savedPass = prefs.getString("SAVED_PASS", "")

        return if (email == savedEmail && pass == savedPass && email.isNotEmpty()) {
            prefs.edit().putBoolean("IS_LOGGED_IN", true).apply()
            true
        } else {
            false
        }
    }

    fun logout() {
        prefs.edit().putBoolean("IS_LOGGED_IN", false).apply()
    }

    fun getName(): String = prefs.getString("SAVED_NAME", "Foodie") ?: "Foodie"
    fun getEmail(): String = prefs.getString("SAVED_EMAIL", "") ?: ""

    fun saveLocation(location: String) {
        prefs.edit().putString("SAVED_LOCATION", location).apply()
    }

    fun getLocation(): String {
        return prefs.getString("SAVED_LOCATION", "Andheri West, Mumbai") ?: "Andheri West, Mumbai"
    }

    // --- NEW: DYNAMIC ORDER HISTORY SYSTEM ---

    fun saveOrder(restaurantName: String, items: String, price: String, date: String) {
        val existingOrders = prefs.getString("PAST_ORDERS", "") ?: ""
        // We separate data with | and orders with @@@ so we don't need external database libraries
        val newOrder = "$restaurantName|$items|$price|$date"

        val updatedOrders = if (existingOrders.isEmpty()) newOrder else "$newOrder@@@$existingOrders"
        prefs.edit().putString("PAST_ORDERS", updatedOrders).apply()
    }

    fun getPastOrders(): List<PastOrder> {
        val existingOrders = prefs.getString("PAST_ORDERS", "") ?: ""
        if (existingOrders.isEmpty()) return emptyList()

        return existingOrders.split("@@@").mapNotNull { orderString ->
            val parts = orderString.split("|")
            if (parts.size == 4) {
                PastOrder(parts[0], parts[1], parts[2], parts[3])
            } else {
                null
            }
        }
    }
}
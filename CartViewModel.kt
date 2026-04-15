package com.example.feastflow

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateMapOf<MenuItem, Int>()

    // --- THIS IS THE MISSING LINE THAT FIXES ALL THE ERRORS ---
    val cartItems: Map<MenuItem, Int> get() = _cartItems
    // ----------------------------------------------------------

    val totalItems: Int get() = _cartItems.values.sum()
    val totalPrice: Int get() = _cartItems.entries.sumOf { it.key.price * it.value }

    fun addItem(item: MenuItem) {
        _cartItems[item] = (_cartItems[item] ?: 0) + 1
    }

    fun removeItem(item: MenuItem) {
        val current = _cartItems[item] ?: 0
        if (current > 1) {
            _cartItems[item] = current - 1
        } else {
            _cartItems.remove(item)
        }
    }

    fun getQuantity(item: MenuItem): Int = _cartItems[item] ?: 0
    fun clearCart() {
        _cartItems.clear()
    }
}
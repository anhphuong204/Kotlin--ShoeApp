package com.example.shoe

import com.example.shoe.model.CartItem
import android.util.Log

object CartManager {
    private val cartItems: MutableList<CartItem> = mutableListOf()

    fun addProductToCart(item: CartItem) {
        // Check if the item is already in the cart
        val existingItemIndex = cartItems.indexOfFirst { it.id == item.id && it.size == item.size && it.color == item.color }
        if (existingItemIndex != -1) {
            // Update the quantity if item already exists
            val existingItem = cartItems[existingItemIndex]
            existingItem.quantity += item.quantity
            Log.d("CartManager", "Updated item quantity: ${existingItem.name}, New quantity: ${existingItem.quantity}")
        } else {
            cartItems.add(item)
            Log.d("CartManager", "Added new item to cart: ${item.name}")
        }
    }

    fun getCartItems(): List<CartItem> = cartItems

    fun updateProductQuantity(item: CartItem, newQuantity: Int) {
        val index = cartItems.indexOfFirst { it.id == item.id && it.size == item.size && it.color == item.color }
        if (index != -1) {
            cartItems[index].quantity = newQuantity
            Log.d("CartManager", "Updated item quantity: ${cartItems[index].name}, New quantity: $newQuantity")
        } else {
            Log.e("CartManager", "Item not found for update: ${item.name}")
        }
    }

    fun removeProductFromCart(item: CartItem) {
        if (cartItems.remove(item)) {
            Log.d("CartManager", "Removed item from cart: ${item.name}")
        } else {
            Log.e("CartManager", "Item not found for removal: ${item.name}")
        }
    }

    fun getTotalPrice(): Double {
        val totalPrice = cartItems.sumOf { it.price * it.quantity }
        Log.d("CartManager", "Total price: $totalPrice")
        return totalPrice
    }

    fun clearCart() {
        cartItems.clear()
        Log.d("CartManager", "Cart cleared")
    }
}

package com.example.shoe.model

data class Order(
    val orderId: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val paymentMethod: String = "",
    val productList: List<CartItem> = emptyList(),
    val orderDate: Long = 0L,
    val totalPrice: Double = 0.0
)

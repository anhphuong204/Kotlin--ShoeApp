package com.example.shoe.model

import java.io.Serializable

data class Product(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val image: Int = 0
) : Serializable


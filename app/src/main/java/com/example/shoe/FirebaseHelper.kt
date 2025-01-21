package com.example.shoe

import com.example.shoe.model.Product
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    private val database = FirebaseDatabase.getInstance().reference

    // Dummy data for banner images with drawable resources
    fun fetchBanner(callback: (List<Int>) -> Unit) {
        val bannerDrawableIds = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )
        callback(bannerDrawableIds)
    }

    // Đồng bộ sản phẩm lên Firebase
    fun syncProducts(products: List<Product>, callback: () -> Unit) {
        val productsRef = database.child("products")
        productsRef.setValue(products).addOnSuccessListener {
            callback()
        }.addOnFailureListener {
            // Handle failure
        }
    }

    // Lấy sản phẩm từ Firebase
    fun fetchProducts(callback: (List<Product>) -> Unit) {
        val productsRef = database.child("products")
        productsRef.get().addOnSuccessListener { dataSnapshot ->
            val products = dataSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
            callback(products)
        }.addOnFailureListener {
            // Handle failure
        }
    }
}

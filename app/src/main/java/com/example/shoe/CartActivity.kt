package com.example.shoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoe.model.CartItem

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var totalPrice: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnBack: ImageButton
    private lateinit var cartAdapter: CartAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        recyclerViewCart = findViewById(R.id.recyclerViewCartt)
        totalPrice = findViewById(R.id.totalPricee)
        btnCheckout = findViewById(R.id.btnCheckoutt)
        btnBack = findViewById(R.id.btnBack)

        // Setup RecyclerView
        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        val cartItems = CartManager.getCartItems().toMutableList()  // Ensure MutableList
        cartAdapter = CartAdapter(cartItems, object : CartAdapter.CartItemListener {
            override fun onQuantityChanged(item: CartItem, newQuantity: Int) {
                // Update the item quantity in CartManager
                CartManager.updateProductQuantity(item, newQuantity)
                // Refresh the UI with updated quantity and price
                updateTotalPrice()
            }

            override fun onRemoveItem(item: CartItem) {
                // Remove the item from CartManager
                CartManager.removeProductFromCart(item)
                // Refresh the cart
                updateCart()
            }
        })
        recyclerViewCart.adapter = cartAdapter

        // Calculate and display total price
        updateTotalPrice()

        btnCheckout.setOnClickListener {
            val total = CartManager.getTotalPrice()
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
                putExtra("totalPrice", total)
            }
            startActivity(intent)
        }

        // Set click listener for back button
        btnBack.setOnClickListener {
            navigateToHome()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCart() {
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateTotalPrice() {
        val total = CartManager.getTotalPrice()
        totalPrice.text = "Tổng: ${total.toVND()}"
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

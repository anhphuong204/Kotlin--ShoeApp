package com.example.shoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shoe.model.CartItem
import com.google.android.material.snackbar.Snackbar

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imageProduct: ImageView
    private lateinit var productTitle: TextView
    private lateinit var descriptionProduct: TextView
    private lateinit var priceProduct: TextView
    private lateinit var radioGroupSizes: RadioGroup
    private lateinit var buttonBack: ImageButton
    private lateinit var firebaseHelper: FirebaseHelper

    private var selectedSize: String? = null
    private var selectedColor: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize views and Firebase helper
        imageProduct = findViewById(R.id.productImage)
        productTitle = findViewById(R.id.productTitle)
        descriptionProduct = findViewById(R.id.descriptionProduct)
        priceProduct = findViewById(R.id.priceProduct)
        radioGroupSizes = findViewById(R.id.radioGroupSizes)
        buttonBack = findViewById(R.id.btnBack)
        firebaseHelper = FirebaseHelper()

        // Get product ID from intent
        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId != -1) {
            fetchProductDetails(productId)
        }

        // Handle "Add to Cart" button click
        findViewById<ImageButton>(R.id.btnAddToCart).setOnClickListener {
            addToCart()
        }

        // Handle "Back" button click
        buttonBack.setOnClickListener {
            finish() // Navigate back to the previous activity
        }

        // Handle size selection
        radioGroupSizes.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            selectedSize = radioButton.text.toString()
        }

        // Handle color selection with a helper function
        setUpColorSelection()
    }

    @SuppressLint("SetTextI18n")
    private fun fetchProductDetails(productId: Int) {
        firebaseHelper.fetchProducts { products ->
            val product = products.find { it.id == productId }
            if (product != null) {
                productTitle.text = product.name
                descriptionProduct.text = product.description
                priceProduct.text = product.price.toVND()
                imageProduct.setImageResource(product.image)
            }
        }
    }

    private fun addToCart() {
        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId != -1) {
            firebaseHelper.fetchProducts { products ->
                val product = products.find { it.id == productId }
                if (product != null) {
                    try {
                        if (selectedSize.isNullOrEmpty() || selectedColor.isNullOrEmpty()) {
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                "Please select both size and color.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            return@fetchProducts
                        }

                        val cartItem = CartItem(
                            id = product.id,
                            name = product.name,
                            size = selectedSize!!,
                            color = selectedColor!!,
                            price = product.price,
                            imageResId = product.image,
                            quantity = 1
                        )

                        // Add product to cart
                        CartManager.addProductToCart(cartItem)

                        // Navigate to CartActivity
                        val intent = Intent(this, CartActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("ProductDetailActivity", "Error adding to cart", e)
                    }
                }
            }
        }
    }

    private fun setUpColorSelection() {
        val colors = mapOf(
            R.id.colorBlue to "Xanh",
            R.id.colorRed to "Đỏ",
            R.id.colorYellow to "Vàng",
            R.id.colorOrange to "Cam",
            R.id.colorViolet to "Tím"
        )

        for ((buttonId, color) in colors) {
            findViewById<ImageButton>(buttonId).setOnClickListener {
                selectedColor = color
            }
        }
    }
}

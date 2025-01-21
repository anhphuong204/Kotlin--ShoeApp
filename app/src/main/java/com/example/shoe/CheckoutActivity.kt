package com.example.shoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoe.model.CartItem
import com.example.shoe.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addressInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var btnConfirm: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var btnBack: ImageButton

    private lateinit var cartItems: MutableList<CartItem>
    private val auth = FirebaseAuth.getInstance()
    private lateinit var cartAdapter: CartAdapter
    private var totalPrice: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewProductss)
        addressInput = findViewById(R.id.addressInputt)
        phoneNumberInput = findViewById(R.id.phoneNumberInputt)
        paymentMethodGroup = findViewById(R.id.paymentMethodGroupp)
        btnConfirm = findViewById(R.id.btnConfirmm)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        btnBack = findViewById(R.id.btnBack)

        // Get cart items and total price from Intent
        cartItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cartItems", CartItem::class.java) ?: mutableListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("cartItems") ?: mutableListOf()
        }
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)

        // Check if cart items are empty
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show()
            finish() // Exit if no cart items
            return
        }

        // Set up RecyclerView
        cartAdapter = CartAdapter(cartItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        // Display total price
        totalPriceTextView.text = "Tổng: ${totalPrice.toVND()}"

        // Handle back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // Close CheckoutActivity or return to CartActivity
            }
        })

        // Confirm button click listener
        btnConfirm.setOnClickListener {
            showOrderSummaryDialog()
        }

        // Back button click listener
        btnBack.setOnClickListener {
            finish() // Close CheckoutActivity
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showOrderSummaryDialog() {
        val address = addressInput.text.toString().trim()
        val phoneNumber = phoneNumberInput.text.toString().trim()
        val selectedPaymentId = paymentMethodGroup.checkedRadioButtonId

        // Validate inputs
        if (address.isEmpty() || phoneNumber.isEmpty() || selectedPaymentId == -1) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected payment method
        val selectedPaymentMethod = findViewById<RadioButton>(selectedPaymentId).text.toString()

        // Format the current date
        val currentDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(currentDate))

        // Create the summary message for products
        val productSummary = cartItems.joinToString(separator = "\n") { item ->
            "- Tên: ${item.name}, Size: ${item.size}, Màu: ${item.color}, Số lượng: ${item.quantity}, Giá: ${item.price.toVND()}"
        }

        val orderSummary = """
            Địa chỉ: $address
            Số điện thoại: $phoneNumber
            Phương thức thanh toán: $selectedPaymentMethod
            Ngày mua: $formattedDate
            Tổng tiền: ${totalPrice.toVND()}
            Sản phẩm:
            $productSummary
        """.trimIndent()

        // Show confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Xác nhận đơn hàng")
            .setMessage(orderSummary)
            .setPositiveButton("Xác nhận") { _, _ ->
                processOrder(address, phoneNumber, selectedPaymentMethod, currentDate)
            }
            .setNegativeButton("Hủy", null) // Just close the dialog
            .show()
    }

    private fun processOrder(address: String, phoneNumber: String, paymentMethod: String, orderDate: Long) {
        val orderId = FirebaseDatabase.getInstance().reference.push().key ?: return

        val order = Order(
            orderId = orderId,
            address = address,
            phoneNumber = phoneNumber,
            paymentMethod = paymentMethod,
            productList = cartItems,
            orderDate = orderDate
        )

        // Reference to the "orders" node in Firebase
        val orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId)

        // Save order to Firebase
        orderRef.setValue(order).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("CheckoutActivity", "Order saved successfully")
                // Clear the cart
                CartManager.clearCart()
                // Navigate to HomeActivity after order confirmation
                Toast.makeText(this, "Đơn hàng đã được đặt thành công!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("CheckoutActivity", "Order saving failed", task.exception)
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show()
            }
        }
    }




}

package com.example.shoe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shoe.model.Product


class HomeActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var btnHome: ImageButton
    private lateinit var btnCart: ImageButton
    private lateinit var btnLogout: ImageButton
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private val firebaseHelper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        searchBar = findViewById(R.id.etSearch)
        recyclerViewProducts = findViewById(R.id.recyclerView)
        btnHome = findViewById(R.id.btnHome)
        btnCart = findViewById(R.id.btnCart)
        btnLogout = findViewById(R.id.btnLogout)
        viewPager = findViewById(R.id.viewPager)

        // Fetch and display banners
        fetchBannerUrls()

        // Sync default products and then fetch them
        syncAndFetchProducts()

        // Set click listeners for buttons
        btnHome.setOnClickListener {
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        btnLogout.setOnClickListener {
            finishAffinity()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun fetchBannerUrls() {
        firebaseHelper.fetchBanner { bannerUrls ->
            if (bannerUrls.isNotEmpty()) {
                viewPager.adapter = BannerAdapter(bannerUrls)
                setUpAutoScroll(bannerUrls.size)
            }
        }
    }

    private fun setUpAutoScroll(size: Int) {
        if (size > 1) {
            val runnable = object : Runnable {
                override fun run() {
                    val currentItem = viewPager.currentItem
                    val nextItem = (currentItem + 1) % size
                    viewPager.setCurrentItem(nextItem, true)
                    handler.postDelayed(this, 3000)
                }
            }
            handler.postDelayed(runnable, 3000)
        }
    }

    private fun syncAndFetchProducts() {
        val defaultProducts = listOf(
            Product(
                id = 1,
                name = "Toddler Canvas Slip",
                description = "Đôi giày phong cách trượt ván với thiết kế slip-on. Hãy diện đôi giày này cho phong cách đơn giản mà cực cool.",
                price = 1500000.0,
                image = R.drawable.sneaker_1
            ),
            Product(
                id = 2,
                name = "Balanshica",
                description = "Xu Hướng Thời Trang Giày Phong Cách Hàn Quốc Thường Ngày Vintage Cặp Đôi Giày Trượt Ván Mũi Tròn Đế Dày Thoải Mái Giày Đi Bộ",
                price = 2990000.0,
                image = R.drawable.sneaker_2
            ),
            Product(
                id = 3,
                name = "Asia Sport",
                description = "Giày Asia là một loại giày có kiểu dáng khá đơn giản, trẻ trung, mạnh mẽ nhưng cũng không kém phần sành điệu, cá tính.",
                price = 1990000.0,
                image = R.drawable.sneaker_3
            ),
            Product(
                id = 4,
                name = "Vans Old Skool",
                description = "Vẻ ngoài đơn giản nhưng cực kì cuốn hút của Vans Old Skool Black đã khiến cho mẫu giày này liên tục nằm trong top best-seller của Vans.",
                price = 2899000.0,
                image = R.drawable.sneaker_4
            )
        )

        // Sync default products to Firebase
        firebaseHelper.syncProducts(defaultProducts) {
            // After syncing, fetch the products
            fetchProducts()
        }
    }

    private fun fetchProducts() {
        firebaseHelper.fetchProducts { products ->
            updateProductsRecyclerView(products)
        }
    }

    private fun updateProductsRecyclerView(products: List<Product>) {
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerViewProducts.layoutManager = gridLayoutManager

        recyclerViewProducts.adapter = ProductsAdapter(products) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

package com.example.shoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var skipButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize Skip Button
        skipButton = findViewById(R.id.skip_button)

        // Set click listener for Skip Button
        skipButton.setOnClickListener {
            // Navigate to LoginActivity
            navigateToLogin()
        }

    }

    private fun navigateToLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the SplashActivity
    }
}

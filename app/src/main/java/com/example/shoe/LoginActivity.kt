package com.example.shoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        loginUsername = findViewById(R.id.login_username)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signupRedirectText = findViewById(R.id.signupRedirectText)

        // Set click listeners
        loginButton.setOnClickListener {
            if (validateUsername() && validatePassword()) {
                checkUser()
            }
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUsername(): Boolean {
        val username = loginUsername.text.toString()
        return if (username.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = loginPassword.text.toString()
        return if (password.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val usernameInput = loginUsername.text.toString().trim()
        val passwordInput = loginPassword.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users")
        val query = reference.orderByChild("username").equalTo(usernameInput)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var passwordFromDB: String? = null
                    var nameFromDB: String? = null
                    var emailFromDB: String? = null
                    var usernameFromDB: String? = null

                    for (userSnapshot in snapshot.children) {
                        passwordFromDB = userSnapshot.child("password").getValue(String::class.java)
                        nameFromDB = userSnapshot.child("name").getValue(String::class.java)
                        emailFromDB = userSnapshot.child("email").getValue(String::class.java)
                        usernameFromDB = userSnapshot.child("username").getValue(String::class.java)
                    }

                    if (passwordFromDB == passwordInput) {
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            putExtra("name", nameFromDB)
                            putExtra("email", emailFromDB)
                            putExtra("username", usernameFromDB)
                            putExtra("password", passwordFromDB)
                        }

                        Log.d("LoginActivity", "Login successful, launching HomeActivity")
                        startActivity(intent)
                        finish() // Close LoginActivity
                    } else {
                        loginPassword.error = "Invalid Credentials"
                        loginPassword.requestFocus()
                        Log.d("LoginActivity", "Invalid credentials provided")
                    }
                } else {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                    Log.d("LoginActivity", "User does not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@LoginActivity,
                    "Database Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("LoginActivity", "Database error: ${error.message}")
            }
        })
    }
}
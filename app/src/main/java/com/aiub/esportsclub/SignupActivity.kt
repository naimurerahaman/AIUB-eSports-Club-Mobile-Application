package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // ===== FIX STATUS BAR OVERLAY =====
// This tells Android: "don't let your app draw behind system bars"
        WindowCompat.setDecorFitsSystemWindows(window, true)

// Set status bar color to match toolbar
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")

// Set navigation bar color to match app background
        window.navigationBarColor = android.graphics.Color.parseColor("#0D0D0D")

// Make status bar icons WHITE (visible on dark background)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars    = false  // false = white icons
            isAppearanceLightNavigationBars = false  // false = white icons
        }
        // Connect to Firebase Auth
        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<Button>(R.id.btnBackSignup)
        val etName = findViewById<EditText>(R.id.etSignupName)
        val etEmail = findViewById<EditText>(R.id.etSignupEmail)
        val etPassword = findViewById<EditText>(R.id.etSignupPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarSignup)

        btnBack.setOnClickListener { finish() }

        btnSignup.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // ===== VALIDATION =====
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                // Firebase requires password to be at least 6 characters
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnSignup.isEnabled = false

            // ===== FIREBASE CREATE USER =====
            // createUserWithEmailAndPassword tells Firebase to create a new account
            // Firebase saves the email and password securely on its servers
            // The password is NEVER stored as plain text — Firebase hashes it
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Save user info to Firestore "users" collection
                    // This is how we count total users in Analytics
                    val userId = auth.currentUser?.uid ?: ""
                    val userDoc = hashMapOf(
                        "email"     to email,
                        "userId"    to userId,
                        "createdAt" to System.currentTimeMillis()
                    )
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .set(userDoc)
                    // Account created successfully!
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Account created! Welcome 🎉", Toast.LENGTH_SHORT).show()

                    // Go to MainActivity after successful signup
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    progressBar.visibility = View.GONE
                    btnSignup.isEnabled = true
                    Toast.makeText(this, "Signup failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// We import Firebase Authentication
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Declare a variable for Firebase Authentication
    // "lateinit" means we will set it up inside onCreate, not right now
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ===== INITIALIZE FIREBASE AUTH =====
        // This line connects us to Firebase Authentication service
        // FirebaseAuth.getInstance() returns the one shared auth object
        auth = FirebaseAuth.getInstance()

        // ===== CHECK IF USER IS ALREADY LOGGED IN =====
        // auth.currentUser is the currently logged-in user
        // If it's not null, someone is already logged in — skip to MainActivity
        if (auth.currentUser != null) {
            goToMainActivity()
            return // Stop executing the rest of onCreate
        }

        // ===== FIND ALL VIEWS =====
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToSignup = findViewById<TextView>(R.id.tvGoToSignup)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)

        // ===== LOGIN BUTTON CLICK =====
        btnLogin.setOnClickListener {

            // Read what the user typed and remove extra spaces
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate — make sure fields are not empty
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show the loading spinner while Firebase is working
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false // Disable button to prevent double-clicking

            // ===== FIREBASE LOGIN =====
            // signInWithEmailAndPassword sends email+password to Firebase
            // Firebase checks if this user exists and password is correct
            // This is an ASYNC operation — it runs in the background
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // This code runs ONLY if login was successful
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Welcome back! ✅", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }
                .addOnFailureListener { exception ->
                    // This code runs ONLY if login failed
                    // "exception.message" contains the reason why it failed
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }

        // ===== GO TO SIGNUP SCREEN =====
        tvGoToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        // Find the admin login link
        val tvAdminLink = findViewById<TextView>(R.id.tvAdminLoginLink)

        // When tapped, go to Admin Login screen
        tvAdminLink.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
    }

    // This function opens MainActivity and clears the back stack
    // "back stack" = the history of screens. We clear it so pressing
    // Back after login doesn't go back to the login screen
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // These flags clear all previous screens from memory
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
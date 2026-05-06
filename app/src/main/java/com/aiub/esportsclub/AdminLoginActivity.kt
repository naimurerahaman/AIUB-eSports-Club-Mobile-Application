package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminLoginActivity : AppCompatActivity() {

    // ===== HARDCODED ADMIN CREDENTIALS =====
    // We define the admin email and password directly in the code
    // This is called "hardcoding" — the values never change
    // In a real professional app, this would be stored securely on a server
    // But for a university project, this approach is perfectly acceptable
    private val ADMIN_EMAIL = "admin@aiub.com"
    private val ADMIN_PASSWORD = "admin111"
    // "private val" means:
    // private = only this file can see this variable
    // val = the value cannot be changed after it is set (constant)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        // Find all the views
        val etEmail = findViewById<EditText>(R.id.etAdminEmail)
        val etPassword = findViewById<EditText>(R.id.etAdminPassword)
        val btnLogin = findViewById<Button>(R.id.btnAdminLogin)
        val tvBack = findViewById<TextView>(R.id.tvBackToUserLogin)

        // ===== LOGIN BUTTON CLICK =====
        btnLogin.setOnClickListener {

            // Read what the admin typed
            val enteredEmail = etEmail.text.toString().trim()
            val enteredPassword = etPassword.text.toString().trim()

            // Check if fields are empty first
            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== THE CORE LOGIC — THIS IS WHAT THE VIVA WILL ASK =====
            // We use an IF statement to compare what was typed
            // with the correct admin credentials
            //
            // Think of it like a door lock:
            // IF the key matches → open the door (go to dashboard)
            // ELSE → door stays locked (show error)
            //
            // "==" means "is exactly equal to"
            // "&&" means "AND" — both conditions must be true
            if (enteredEmail == ADMIN_EMAIL && enteredPassword == ADMIN_PASSWORD) {

                // ✅ BOTH email AND password are correct
                Toast.makeText(this, "Welcome, Admin! 🛡️", Toast.LENGTH_SHORT).show()

                // Open the Admin Dashboard
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish() // Close the login screen so Back button can't return here

            } else {

                // ❌ Either email OR password is wrong
                // We don't tell them WHICH one is wrong (security reason)
                Toast.makeText(this, "Invalid admin credentials!", Toast.LENGTH_LONG).show()

            }
        }

        // Back to normal user login
        tvBack.setOnClickListener {
            finish() // Just close this screen — goes back to LoginActivity
        }
    }
}
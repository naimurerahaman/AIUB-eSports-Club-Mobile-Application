package com.aiub.esportsclub

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Import Firebase Authentication
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    // Declare Firebase Auth variable
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // ===== CONNECT TO FIREBASE AUTH =====
        // FirebaseAuth.getInstance() gives us access to all auth functions
        auth = FirebaseAuth.getInstance()

        // ===== FIND ALL VIEWS =====
        val etEmail       = findViewById<EditText>(R.id.etForgotEmail)
        val btnSendReset  = findViewById<Button>(R.id.btnSendReset)
        val progressBar   = findViewById<ProgressBar>(R.id.progressBarForgot)
        val layoutSuccess = findViewById<LinearLayout>(R.id.layoutSuccessForgot)
        val tvSuccessMsg  = findViewById<TextView>(R.id.tvSuccessEmailSent)
        val tvBack        = findViewById<TextView>(R.id.tvBackToLogin)

        // ===== BACK BUTTON =====
        tvBack.setOnClickListener {
            finish() // Close this screen and go back to Login
        }

        // ===== SEND RESET EMAIL BUTTON =====
        btnSendReset.setOnClickListener {

            // Read what the user typed and remove extra spaces
            val email = etEmail.text.toString().trim()

            // ===== VALIDATION =====
            // Check if the email field is empty
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop here, don't continue
            }

            // Show the loading spinner while Firebase is working
            // Hide the button so user cannot click it again while loading
            progressBar.visibility = View.VISIBLE
            btnSendReset.isEnabled = false
            btnSendReset.text = "Sending..."

            // ===== FIREBASE PASSWORD RESET =====
            // sendPasswordResetEmail() is the Firebase function that does everything
            //
            // What Firebase does behind the scenes:
            // 1. Checks if this email is registered in our app
            // 2. If yes → generates a unique secure reset link
            // 3. Sends an email to that address with the link
            // 4. When user clicks the link → they can enter a new password
            // 5. Firebase saves the new password securely
            //
            // All of this happens automatically — we just call one function!
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    // ✅ Firebase successfully sent the reset email

                    // Hide the spinner
                    progressBar.visibility = View.GONE

                    // Show the green success box
                    // "gone" → "visible" makes it appear on screen
                    layoutSuccess.visibility = View.VISIBLE

                    // Show a helpful message telling the user what to do next
                    tvSuccessMsg.text = "A password reset link has been sent to:\n$email\n\nPlease check your inbox and spam folder."

                    // Hide the send button — no need to send again
                    btnSendReset.visibility = View.GONE

                    // Show a toast as well
                    Toast.makeText(
                        this,
                        "Reset email sent! Check your inbox. ✅",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { exception ->
                    // ❌ Something went wrong

                    // Hide the spinner and re-enable the button
                    progressBar.visibility = View.GONE
                    btnSendReset.isEnabled = true
                    btnSendReset.text = "📧  Send Reset Email"

                    // Show the exact error from Firebase
                    Toast.makeText(
                        this,
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}
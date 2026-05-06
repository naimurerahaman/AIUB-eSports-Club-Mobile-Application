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

// Import Firebase Firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    // Declare Firestore database variable
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // ===== INITIALIZE FIREBASE =====
        // FirebaseFirestore.getInstance() connects us to our Firestore database
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find all views
        val btnBack = findViewById<Button>(R.id.btnBackReg)
        val etName = findViewById<EditText>(R.id.etName)
        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val etTeamName = findViewById<EditText>(R.id.etTeamName)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val layoutSuccess = findViewById<LinearLayout>(R.id.layoutSuccess)
        val tvSuccessMessage = findViewById<TextView>(R.id.tvSuccessMessage)

        // Add a ProgressBar to your XML if not already there
        // For now we'll use the button text as a loading indicator

        btnBack.setOnClickListener { finish() }

        btnSubmit.setOnClickListener {

            val name = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val teamName = etTeamName.text.toString().trim()

            // ===== VALIDATION =====
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studentId.isEmpty()) {
                Toast.makeText(this, "Please enter your Student ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (teamName.isEmpty()) {
                Toast.makeText(this, "Please enter your team name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== PREPARE DATA FOR FIRESTORE =====
            // We create a HashMap — think of it like a labeled box
            // Each key is the field name, each value is the data
            val registrationData = hashMapOf(
                "name" to name,
                "studentId" to studentId,
                "teamName" to teamName,
                // auth.currentUser?.uid gets the unique ID of the logged-in user
                // "?." means "only do this if currentUser is not null"
                "userId" to (auth.currentUser?.uid ?: "unknown"),
                "userEmail" to (auth.currentUser?.email ?: "unknown"),
                // System.currentTimeMillis() gives current time in milliseconds
                "timestamp" to System.currentTimeMillis()
            )

            // Show loading state
            btnSubmit.isEnabled = false
            btnSubmit.text = "Submitting..."

            // ===== SAVE TO FIRESTORE =====
            // db.collection("registrations") — go to the "registrations" collection
            // .add(registrationData) — add our data as a new document
            // Firebase automatically creates a unique ID for the document
            db.collection("registrations")
                .add(registrationData)
                .addOnSuccessListener { documentReference ->
                    // documentReference.id is the unique ID Firebase gave this document
                    // We don't need to use it, but it's good to know it exists

                    btnSubmit.isEnabled = true
                    btnSubmit.text = "✅ Submit Registration"

                    // Show the success message
                    val message = "Name: $name\nStudent ID: $studentId\nTeam: $teamName\n\nYour registration has been saved online! ☁️"
                    tvSuccessMessage.text = message
                    layoutSuccess.visibility = View.VISIBLE
                    btnSubmit.visibility = View.GONE

                    Toast.makeText(this, "Registration saved to Firebase! ✅", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    // Something went wrong — show the error
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "✅ Submit Registration"
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
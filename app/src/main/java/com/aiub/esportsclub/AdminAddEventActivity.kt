package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddEventActivity : AppCompatActivity() {

    // Declare the Firestore database variable
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_event)

        // Connect to Firestore
        db = FirebaseFirestore.getInstance()

        // Find all views
        val btnBack = findViewById<Button>(R.id.btnBackAddEvent)
        val etName = findViewById<EditText>(R.id.etEventName)
        val etGame = findViewById<EditText>(R.id.etGameName)
        val etDate = findViewById<EditText>(R.id.etEventDate)
        val etPrize = findViewById<EditText>(R.id.etEventPrize)
        val etDesc = findViewById<EditText>(R.id.etEventDescription)
        val btnSave = findViewById<Button>(R.id.btnSaveEvent)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {

            // Read all input values
            val name = etName.text.toString().trim()
            val game = etGame.text.toString().trim()
            val date = etDate.text.toString().trim()
            val prize = etPrize.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            // ===== VALIDATION =====
            // Check every field is filled before saving
            if (name.isEmpty() || game.isEmpty() || date.isEmpty() ||
                prize.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill in ALL fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== BUILD THE DATA OBJECT =====
            // HashMap is like a labeled box with compartments
            // Each compartment has a label (key) and a value
            val eventData = hashMapOf(
                "name"        to name,
                "game"        to game,
                "date"        to date,
                "prize"       to prize,
                "description" to desc,
                "timestamp"   to System.currentTimeMillis()
                // timestamp helps us sort events by when they were added
            )

            // Show loading state on button
            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            // ===== SAVE TO FIRESTORE =====
            // "events" is our collection name
            // .add() creates a NEW document with a random ID
            // Firebase picks a unique ID automatically — we don't need to worry about it
            db.collection("events")
                .add(eventData)
                .addOnSuccessListener { documentReference ->
                    // documentReference.id is the unique ID Firebase gave this document
                    // We log it but we don't need to show it to the admin

                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Event to Firebase"

                    Toast.makeText(this, "Event saved successfully! ✅", Toast.LENGTH_SHORT).show()

                    // Clear all fields so admin can add another event easily
                    etName.text.clear()
                    etGame.text.clear()
                    etDate.text.clear()
                    etPrize.text.clear()
                    etDesc.text.clear()
                }
                .addOnFailureListener { exception ->
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Event to Firebase"
                    Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
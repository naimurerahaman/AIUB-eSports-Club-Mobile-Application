package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddPlayerActivity : AppCompatActivity() {

    // Declare the Firestore variable
    // "lateinit" means we will assign it inside onCreate, not right now
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_player)

        // Connect to Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // ===== FIND ALL VIEWS BY THEIR ID =====
        val btnBack      = findViewById<Button>(R.id.btnBackAddPlayer)
        val etName       = findViewById<EditText>(R.id.etPlayerName)
        val etGame       = findViewById<EditText>(R.id.etPlayerGame)
        val etRank       = findViewById<EditText>(R.id.etPlayerRank)
        val etRole       = findViewById<EditText>(R.id.etPlayerRole)
        val btnSave      = findViewById<Button>(R.id.btnSavePlayer)

        // Back button simply closes this screen and goes back
        btnBack.setOnClickListener {
            finish()
        }

        // ===== SAVE BUTTON CLICK =====
        btnSave.setOnClickListener {

            // Read what the admin typed in each field
            // .trim() removes any accidental spaces at the start or end
            val name = etName.text.toString().trim()
            val game = etGame.text.toString().trim()
            val rank = etRank.text.toString().trim()
            val role = etRole.text.toString().trim()

            // ===== VALIDATION =====
            // Check every field is filled before we try to save
            // If any field is empty, show a message and stop
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter the player name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop here — don't run code below
            }
            if (game.isEmpty()) {
                Toast.makeText(this, "Please enter the game specialty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (rank.isEmpty()) {
                Toast.makeText(this, "Please enter the player rank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (role.isEmpty()) {
                Toast.makeText(this, "Please enter the player role!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== BUILD THE DATA OBJECT =====
            // hashMapOf creates a key-value pair container
            // Think of it like a form where each field has a label and a value
            // This is what gets stored as one document in Firestore
            val playerData = hashMapOf(
                "name"      to name,
                "game"      to game,
                "rank"      to rank,
                "role"      to role,
                "timestamp" to System.currentTimeMillis()
                // timestamp = current time in milliseconds
                // We use this to order players by when they were added
            )

            // Show loading state so the admin knows the app is working
            btnSave.isEnabled = false   // Disable button to prevent double-clicking
            btnSave.text = "Saving..."

            // ===== SAVE TO FIRESTORE =====
            // db.collection("players") → go to the "players" collection
            // .add(playerData)         → create a NEW document with a random ID
            // Firebase automatically generates a unique ID for each document
            db.collection("players")
                .add(playerData)
                .addOnSuccessListener {
                    // This runs ONLY when save was successful

                    // Re-enable the button and restore its text
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Player to Firebase"

                    // Show success message
                    Toast.makeText(this, "Player added successfully! ✅", Toast.LENGTH_SHORT).show()

                    // Clear all fields so admin can add another player easily
                    etName.text.clear()
                    etGame.text.clear()
                    etRank.text.clear()
                    etRole.text.clear()
                }
                .addOnFailureListener { exception ->
                    // This runs ONLY when save failed
                    // exception.message tells us what went wrong

                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Player to Firebase"

                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditPlayerActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // We store the document ID here so we know WHICH player to update in Firestore
    // Without this, we would not know which of the many documents to change
    private var documentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_player)

        // Connect to Firestore
        db = FirebaseFirestore.getInstance()

        // Find all views
        val btnBack   = findViewById<Button>(R.id.btnBackEditPlayer)
        val etName    = findViewById<EditText>(R.id.etEditPlayerName)
        val etGame    = findViewById<EditText>(R.id.etEditPlayerGame)
        val etRank    = findViewById<EditText>(R.id.etEditPlayerRank)
        val etRole    = findViewById<EditText>(R.id.etEditPlayerRole)
        val btnUpdate = findViewById<Button>(R.id.btnUpdatePlayer)

        btnBack.setOnClickListener { finish() }

        // ===== STEP 1: RECEIVE DATA SENT FROM AdminPlayerAdapter =====
        // Remember in the adapter we used intent.putExtra() to send data?
        // Now we read it here using intent.getStringExtra()
        // The key string (e.g. "documentId") must match EXACTLY what was used in putExtra
        documentId        = intent.getStringExtra("documentId") ?: ""
        val receivedName  = intent.getStringExtra("name")       ?: ""
        val receivedGame  = intent.getStringExtra("game")       ?: ""
        val receivedRank  = intent.getStringExtra("rank")       ?: ""
        val receivedRole  = intent.getStringExtra("role")       ?: ""

        // ?: "" is the Elvis operator in Kotlin
        // It means: if the value is null, use empty string instead

        // ===== STEP 2: PRE-FILL THE FIELDS WITH EXISTING DATA =====
        // This is what makes it an "Edit" screen and not an "Add" screen
        // The admin sees the current data already in the fields
        // and can just change the parts they want to update
        etName.setText(receivedName)
        etGame.setText(receivedGame)
        etRank.setText(receivedRank)
        etRole.setText(receivedRole)

        // ===== STEP 3: UPDATE BUTTON CLICK =====
        btnUpdate.setOnClickListener {

            // Read whatever is now in the fields (could be original or changed)
            val newName = etName.text.toString().trim()
            val newGame = etGame.text.toString().trim()
            val newRank = etRank.text.toString().trim()
            val newRole = etRole.text.toString().trim()

            // Validate — make sure no field is empty
            if (newName.isEmpty()) {
                Toast.makeText(this, "Player name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newGame.isEmpty()) {
                Toast.makeText(this, "Game cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newRank.isEmpty()) {
                Toast.makeText(this, "Rank cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newRole.isEmpty()) {
                Toast.makeText(this, "Role cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== BUILD THE UPDATED DATA =====
            // mapOf creates a simple read-only key-value map
            // We only include the fields we want to change
            // The "timestamp" field is NOT included here — it stays as the original creation time
            val updatedData = mapOf(
                "name" to newName,
                "game" to newGame,
                "rank" to newRank,
                "role" to newRole
            )

            // Show loading state
            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."

            // ===== SEND UPDATE TO FIRESTORE =====
            // .document(documentId) → finds the EXACT document we want to change
            // .update(updatedData)  → only changes the fields listed in updatedData
            //                         all other fields in the document stay the same
            //
            // This is DIFFERENT from .set() which would overwrite the ENTIRE document
            // .update() is safer because it only touches what we specify
            db.collection("players")
                .document(documentId)
                .update(updatedData)
                .addOnSuccessListener {
                    // Update was successful

                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Player"

                    Toast.makeText(this, "Player updated successfully! ✅", Toast.LENGTH_SHORT).show()

                    // Go back to the Manage Players screen
                    // When we go back, onResume() will reload the data
                    // so the updated player info is shown immediately
                    finish()
                }
                .addOnFailureListener { exception ->
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Player"
                    Toast.makeText(this, "Update failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
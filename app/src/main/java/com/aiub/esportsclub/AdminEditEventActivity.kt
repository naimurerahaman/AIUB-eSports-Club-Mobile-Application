package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditEventActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // We will store the document ID here
    // We need it to tell Firestore WHICH document to update
    private var documentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_event)

        db = FirebaseFirestore.getInstance()

        val btnBack = findViewById<Button>(R.id.btnBackEdit)
        val etName = findViewById<EditText>(R.id.etEditEventName)
        val etGame = findViewById<EditText>(R.id.etEditGameName)
        val etDate = findViewById<EditText>(R.id.etEditEventDate)
        val etPrize = findViewById<EditText>(R.id.etEditEventPrize)
        val etDesc = findViewById<EditText>(R.id.etEditEventDescription)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateEvent)

        btnBack.setOnClickListener { finish() }

        // ===== RECEIVE DATA FROM THE PREVIOUS SCREEN =====
        // Remember in AdminEventAdapter we used intent.putExtra() to send data
        // Here we receive it using intent.getStringExtra()
        documentId = intent.getStringExtra("documentId") ?: ""
        val receivedName  = intent.getStringExtra("name") ?: ""
        val receivedGame  = intent.getStringExtra("game") ?: ""
        val receivedDate  = intent.getStringExtra("date") ?: ""
        val receivedPrize = intent.getStringExtra("prize") ?: ""
        val receivedDesc  = intent.getStringExtra("description") ?: ""

        // ===== PRE-FILL THE FIELDS WITH EXISTING DATA =====
        // This is the key part of "Edit" — the user sees the OLD data
        // already filled in, so they only need to change what they want
        etName.setText(receivedName)
        etGame.setText(receivedGame)
        etDate.setText(receivedDate)
        etPrize.setText(receivedPrize)
        etDesc.setText(receivedDesc)

        // ===== UPDATE BUTTON =====
        btnUpdate.setOnClickListener {

            val newName  = etName.text.toString().trim()
            val newGame  = etGame.text.toString().trim()
            val newDate  = etDate.text.toString().trim()
            val newPrize = etPrize.text.toString().trim()
            val newDesc  = etDesc.text.toString().trim()

            if (newName.isEmpty() || newGame.isEmpty() || newDate.isEmpty() ||
                newPrize.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== BUILD THE UPDATE DATA =====
            // mapOf creates a Map (similar to HashMap but simpler for updates)
            // We only include the fields we want to change
            val updatedData = mapOf(
                "name"        to newName,
                "game"        to newGame,
                "date"        to newDate,
                "prize"       to newPrize,
                "description" to newDesc
                // We do NOT update the timestamp — we keep the original creation time
            )

            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."

            // ===== SEND UPDATE TO FIRESTORE =====
            // .document(documentId) — finds the EXACT document we want to change
            // .update(updatedData) — only changes the fields in our map
            // The other fields (like timestamp) stay exactly the same
            db.collection("events")
                .document(documentId)
                .update(updatedData)
                .addOnSuccessListener {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Event"
                    Toast.makeText(this, "Event updated successfully! ✅", Toast.LENGTH_SHORT).show()
                    finish() // Go back to the manage events screen
                }
                .addOnFailureListener { exception ->
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Event"
                    Toast.makeText(this, "Update failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
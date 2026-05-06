package com.aiub.esportsclub

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminManagePlayersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminPlayerAdapter

    // mutableListOf() creates an empty MutableList
    // We will fill it with player data from Firestore
    private val playerList = mutableListOf<AdminPlayer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_players)

        // Connect to Firestore
        db = FirebaseFirestore.getInstance()

        // Find views
        val btnBack     = findViewById<Button>(R.id.btnBackManagePlayers)
        val progressBar = findViewById<ProgressBar>(R.id.progressManagePlayers)
        val tvNoPlayers = findViewById<TextView>(R.id.tvNoPlayers)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminPlayers)

        btnBack.setOnClickListener { finish() }

        // ===== SET UP RECYCLERVIEW =====
        // LinearLayoutManager arranges items in a straight vertical list
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create the adapter and connect it to the RecyclerView
        adapter = AdminPlayerAdapter(this, playerList)
        recyclerView.adapter = adapter
    }

    // ===== onResume =====
    // onResume() is called every time this screen becomes visible again
    // For example: after you go to Edit screen and press Back to return here
    // We reload data so any edits made on the Edit screen are shown immediately
    override fun onResume() {
        super.onResume()

        // Clear the old list first so we don't show duplicate items
        playerList.clear()
        adapter.notifyDataSetChanged()

        // Load fresh data from Firestore
        loadPlayers()
    }

    // ===== LOAD PLAYERS FUNCTION =====
    private fun loadPlayers() {

        val progressBar  = findViewById<ProgressBar>(R.id.progressManagePlayers)
        val tvNoPlayers  = findViewById<TextView>(R.id.tvNoPlayers)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminPlayers)

        // Show spinner, hide list while loading
        progressBar.visibility  = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvNoPlayers.visibility  = View.GONE

        // ===== FETCH ALL PLAYERS FROM FIRESTORE =====
        // db.collection("players")  → go to the players collection
        // .orderBy("timestamp")     → sort by when they were added (oldest first)
        // .get()                    → fetch all documents
        db.collection("players")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // querySnapshot contains ALL the documents we fetched

                // Hide spinner
                progressBar.visibility = View.GONE

                // Check if there are no players at all
                if (querySnapshot.isEmpty) {
                    tvNoPlayers.visibility = View.VISIBLE
                    return@addOnSuccessListener  // Stop here
                }

                // ===== CONVERT DOCUMENTS TO AdminPlayer OBJECTS =====
                // Loop through each document in the snapshot
                for (document in querySnapshot.documents) {

                    // document.id       → the unique Firebase-generated ID
                    // document.getString("name") → reads the "name" field value
                    // ?: "" means: if the field is missing or null, use empty string instead
                    val player = AdminPlayer(
                        documentId = document.id,
                        name       = document.getString("name") ?: "",
                        game       = document.getString("game") ?: "",
                        rank       = document.getString("rank") ?: "",
                        role       = document.getString("role") ?: ""
                    )

                    // Add this player to our list
                    playerList.add(player)
                }

                // Show the list and tell the adapter the data has changed
                recyclerView.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
                // notifyDataSetChanged() tells the RecyclerView:
                // "the data is ready — please draw all the cards now"
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error loading players: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
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

class AdminManageEventsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminEventAdapter
    private val eventList = mutableListOf<AdminEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_events)

        db = FirebaseFirestore.getInstance()

        val btnBack = findViewById<Button>(R.id.btnBackManageEvents)
        val progressBar = findViewById<ProgressBar>(R.id.progressManageEvents)
        val tvNoEvents = findViewById<TextView>(R.id.tvNoEvents)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminEvents)

        btnBack.setOnClickListener { finish() }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminEventAdapter(this, eventList)
        recyclerView.adapter = adapter

        // ===== LOAD EVENTS FROM FIRESTORE =====
        db.collection("events")
            .orderBy("timestamp") // Show oldest events first
            .get()
            .addOnSuccessListener { querySnapshot ->

                progressBar.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvNoEvents.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // Loop through each document and convert to AdminEvent object
                for (document in querySnapshot.documents) {
                    val event = AdminEvent(
                        documentId  = document.id, // This is the unique Firestore ID
                        name        = document.getString("name") ?: "",
                        game        = document.getString("game") ?: "",
                        date        = document.getString("date") ?: "",
                        prize       = document.getString("prize") ?: "",
                        description = document.getString("description") ?: ""
                    )
                    eventList.add(event)
                }

                // Show the list
                recyclerView.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
                // notifyDataSetChanged() tells the RecyclerView:
                // "The data has changed — please redraw everything"
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // onResume is called every time this screen becomes visible again
    // For example: after coming back from the Edit screen
    // We reload the data so any edits are immediately reflected
    override fun onResume() {
        super.onResume()
        // Clear the current list and reload from Firebase
        eventList.clear()
        adapter.notifyDataSetChanged()
        loadEvents()
    }

    private fun loadEvents() {
        val progressBar = findViewById<ProgressBar>(R.id.progressManageEvents)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminEvents)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        db.collection("events")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE

                for (document in querySnapshot.documents) {
                    val event = AdminEvent(
                        documentId  = document.id,
                        name        = document.getString("name") ?: "",
                        game        = document.getString("game") ?: "",
                        date        = document.getString("date") ?: "",
                        prize       = document.getString("prize") ?: "",
                        description = document.getString("description") ?: ""
                    )
                    eventList.add(event)
                }

                if (eventList.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
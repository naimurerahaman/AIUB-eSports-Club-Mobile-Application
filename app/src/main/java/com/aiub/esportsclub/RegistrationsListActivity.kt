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

class RegistrationsListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrations_list)

        db = FirebaseFirestore.getInstance()

        val btnBack = findViewById<Button>(R.id.btnBackRegList)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarList)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyMessage)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRegistrations)

        btnBack.setOnClickListener { finish() }

        // ===== READ DATA FROM FIRESTORE =====
        // db.collection("registrations") — go to the registrations collection
        // .get() — fetch ALL documents in that collection
        db.collection("registrations")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // querySnapshot contains ALL the documents we fetched
                // querySnapshot.documents is a list of all documents

                progressBar.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    // No documents found — show empty message
                    tvEmpty.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // ===== CONVERT DOCUMENTS TO REGISTRATION OBJECTS =====
                val registrationList = mutableListOf<Registration>()

                // Loop through each document
                for (document in querySnapshot.documents) {
                    // document.getString("name") reads the "name" field from the document
                    // ?: "" means if the field doesn't exist, use empty string
                    val name = document.getString("name") ?: ""
                    val studentId = document.getString("studentId") ?: ""
                    val teamName = document.getString("teamName") ?: ""

                    // Create a Registration object and add to our list
                    val registration = Registration(name, studentId, teamName)
                    registrationList.add(registration)
                }

                // Show the list
                recyclerView.visibility = View.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = RegistrationAdapter(this, registrationList)
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to load: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
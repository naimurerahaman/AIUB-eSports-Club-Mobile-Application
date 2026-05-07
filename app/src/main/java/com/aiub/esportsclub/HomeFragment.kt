package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var db  : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find all views
        val tvWelcome     = view.findViewById<TextView>(R.id.tvWelcomeMessage)
        val progressBar   = view.findViewById<ProgressBar>(R.id.progressBarFeed)
        val recyclerView  = view.findViewById<RecyclerView>(R.id.recyclerViewFeed)
        val layoutEmpty   = view.findViewById<LinearLayout>(R.id.layoutEmptyFeed)

        // ===== SHOW PERSONALISED WELCOME MESSAGE =====
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email      = currentUser.email ?: ""
            val name       = currentUser.displayName
            val displayName = if (!name.isNullOrEmpty()) name
            else email.substringBefore("@")
            tvWelcome.text = "Welcome back, $displayName! 👾"
        }

        // ===== SET UP RECYCLERVIEW =====
        // LinearLayoutManager arranges cards in a vertical list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ===== LOAD UPDATES FROM FIREBASE =====
        loadUpdates(progressBar, recyclerView, layoutEmpty)
    }

    // ===== LOAD UPDATES FUNCTION =====
    private fun loadUpdates(
        progressBar : ProgressBar,
        recyclerView: RecyclerView,
        layoutEmpty : LinearLayout
    ) {
        // Show spinner while loading
        progressBar.visibility = View.VISIBLE

        // ===== FETCH FROM FIRESTORE =====
        // db.collection("updates")     → go to the "updates" collection
        // .orderBy("timestamp", DESCENDING) → show newest posts FIRST
        // .get()                        → fetch all documents
        db.collection("updates")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->

                // Hide spinner
                progressBar.visibility = View.GONE

                // Check if there are no posts
                if (querySnapshot.isEmpty) {
                    layoutEmpty.visibility  = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                // ===== CONVERT DOCUMENTS TO UPDATE OBJECTS =====
                val updateList = mutableListOf<Update>()

                for (document in querySnapshot.documents) {
                    // toObject() automatically converts the document fields
                    // into our Update data class
                    // This works because our data class field names match
                    // the Firestore field names exactly
                    val update = document.toObject(Update::class.java)
                    if (update != null) {
                        // Copy the document ID into our object
                        updateList.add(update.copy(documentId = document.id))
                    }
                }

                // Show the RecyclerView with data
                recyclerView.visibility = View.VISIBLE
                layoutEmpty.visibility  = View.GONE

                // Create adapter and connect to RecyclerView
                val adapter = UpdateAdapter(requireContext(), updateList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Failed to load updates: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
package com.aiub.esportsclub

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // Find analytics button
        val btnAnalytics = view.findViewById<Button>(R.id.btnViewAnalytics)

// Navigate to analytics
        btnAnalytics.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminAnalyticsFragment())
        }

        // ===== FIND ALL VIEWS =====
        val btnAddEvent      = view.findViewById<Button>(R.id.btnAddEvent)
        val btnManageEvents  = view.findViewById<Button>(R.id.btnManageEvents)
        val btnAddPlayer     = view.findViewById<Button>(R.id.btnAddPlayer)
        val btnManagePlayers = view.findViewById<Button>(R.id.btnManagePlayers)
        val btnCreateUpdate  = view.findViewById<Button>(R.id.btnCreateUpdate)
        val btnManageUpdates = view.findViewById<Button>(R.id.btnManageUpdates)
        val btnLogout        = view.findViewById<Button>(R.id.btnAdminLogout)
        val switchReg        = view.findViewById<SwitchMaterial>(R.id.switchRegOpen)
        val tvRegStatus      = view.findViewById<TextView>(R.id.tvRegStatus)
        val progressReg      = view.findViewById<ProgressBar>(R.id.progressRegStatus)

        // ===== LOAD REGISTRATION STATUS FROM FIREBASE =====
        loadRegistrationStatus(switchReg, tvRegStatus, progressReg)

        // ===== NAVIGATION BUTTONS =====
        btnAddEvent.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminAddEventFragment())
        }

        btnManageEvents.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminManageEventsFragment())
        }

        btnAddPlayer.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminAddPlayerFragment())
        }

        btnManagePlayers.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminManagePlayersFragment())
        }

        btnCreateUpdate.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminCreateUpdateFragment())
        }

        btnManageUpdates.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminManageUpdatesFragment())
        }

        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AdminLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // ===== READ REGISTRATION STATUS FROM FIRESTORE =====
    private fun loadRegistrationStatus(
        switchReg  : SwitchMaterial,
        tvStatus   : TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE
        switchReg.isEnabled    = false

        db.collection("settings")
            .document("registration")
            .get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                switchReg.isEnabled    = true

                val isOpen = document.getBoolean("isOpen") ?: false

                // Set switch BEFORE listener to avoid triggering it
                switchReg.setOnCheckedChangeListener(null)
                switchReg.isChecked = isOpen
                updateStatusText(tvStatus, isOpen)

                // Now attach listener
                switchReg.setOnCheckedChangeListener { _, isChecked ->
                    saveRegistrationStatus(isChecked, tvStatus)
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                switchReg.isEnabled    = true
                tvStatus.text          = "Error loading status"
                tvStatus.setTextColor(Color.RED)

                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ===== SAVE STATUS TO FIRESTORE =====
    private fun saveRegistrationStatus(isOpen: Boolean, tvStatus: TextView) {
        db.collection("settings")
            .document("registration")
            .set(mapOf("isOpen" to isOpen))
            .addOnSuccessListener {
                updateStatusText(tvStatus, isOpen)
                val message = if (isOpen) "Registration OPENED" else "Registration CLOSED"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ===== UPDATE STATUS TEXT COLOR AND MESSAGE =====
    private fun updateStatusText(tvStatus: TextView, isOpen: Boolean) {
        if (isOpen) {
            tvStatus.text = "Registration is OPEN"
            tvStatus.setTextColor(Color.parseColor("#2ECC71")) // green
        } else {
            tvStatus.text = "Registration is CLOSED"
            tvStatus.setTextColor(Color.parseColor("#E74C3C")) // red
        }
    }
}
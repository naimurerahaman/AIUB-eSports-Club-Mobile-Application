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

        // Navigation buttons
        val btnAnalytics     = view.findViewById<Button>(R.id.btnViewAnalytics)
        val btnAddEvent      = view.findViewById<Button>(R.id.btnAddEvent)
        val btnManageEvents  = view.findViewById<Button>(R.id.btnManageEvents)
        val btnAddPlayer     = view.findViewById<Button>(R.id.btnAddPlayer)
        val btnManagePlayers = view.findViewById<Button>(R.id.btnManagePlayers)
        val btnCreateUpdate  = view.findViewById<Button>(R.id.btnCreateUpdate)
        val btnManageUpdates = view.findViewById<Button>(R.id.btnManageUpdates)
        val btnLogout        = view.findViewById<Button>(R.id.btnAdminLogout)

        // Registration control
        val switchReg        = view.findViewById<SwitchMaterial>(R.id.switchRegOpen)
        val tvRegStatus      = view.findViewById<TextView>(R.id.tvRegStatus)
        val progressReg      = view.findViewById<ProgressBar>(R.id.progressRegStatus)
        // Find the button
        val btnViewRegistrations = view.findViewById<Button>(R.id.btnViewRegistrations)

        // Navigate to view registrations
        btnViewRegistrations.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminViewRegistrationsFragment())
        }
        // Recruitment control
        val switchRecruit    = view.findViewById<SwitchMaterial>(R.id.switchRecruitOpen)
        val tvRecruitStatus  = view.findViewById<TextView>(R.id.tvRecruitStatus)
        val progressRecruit  = view.findViewById<ProgressBar>(R.id.progressRecruitStatus)
        val btnViewApps      = view.findViewById<Button>(R.id.btnViewMemberApplications)

        // Load statuses
        loadRegistrationStatus(switchReg, tvRegStatus, progressReg)
        loadRecruitmentStatus(switchRecruit, tvRecruitStatus, progressRecruit)

        // View member applications
        btnViewApps.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminMemberApplicationsFragment())
        }

        // Analytics
        btnAnalytics.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminAnalyticsFragment())
        }

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

    // ===== REGISTRATION STATUS =====
    private fun loadRegistrationStatus(
        switchReg  : SwitchMaterial,
        tvStatus   : TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE
        switchReg.isEnabled    = false

        db.collection("settings").document("registration").get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                switchReg.isEnabled    = true

                val isOpen = document.getBoolean("isOpen") ?: false
                switchReg.setOnCheckedChangeListener(null)
                switchReg.isChecked = isOpen
                updateRegStatusText(tvStatus, isOpen)

                switchReg.setOnCheckedChangeListener { _, isChecked ->
                    db.collection("settings").document("registration")
                        .set(mapOf("isOpen" to isChecked))
                        .addOnSuccessListener {
                            updateRegStatusText(tvStatus, isChecked)
                            Toast.makeText(
                                requireContext(),
                                if (isChecked) "Registration OPENED" else "Registration CLOSED",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                switchReg.isEnabled    = true
                tvStatus.text          = "Error loading"
            }
    }

    private fun updateRegStatusText(tvStatus: TextView, isOpen: Boolean) {
        tvStatus.text = if (isOpen) "Registration is OPEN" else "Registration is CLOSED"
        tvStatus.setTextColor(
            Color.parseColor(if (isOpen) "#2ECC71" else "#E74C3C")
        )
    }

    // ===== RECRUITMENT STATUS =====
    private fun loadRecruitmentStatus(
        switchRecruit: SwitchMaterial,
        tvStatus     : TextView,
        progressBar  : ProgressBar
    ) {
        progressBar.visibility  = View.VISIBLE
        switchRecruit.isEnabled = false

        // Read from recruitmentStatus → membership → isOpen
        db.collection("recruitmentStatus").document("membership").get()
            .addOnSuccessListener { document ->
                progressBar.visibility  = View.GONE
                switchRecruit.isEnabled = true

                val isOpen = document.getBoolean("isOpen") ?: false
                switchRecruit.setOnCheckedChangeListener(null)
                switchRecruit.isChecked = isOpen
                updateRecruitStatusText(tvStatus, isOpen)

                switchRecruit.setOnCheckedChangeListener { _, isChecked ->
                    // Save new status to Firestore
                    db.collection("recruitmentStatus").document("membership")
                        .set(mapOf("isOpen" to isChecked))
                        .addOnSuccessListener {
                            updateRecruitStatusText(tvStatus, isChecked)
                            Toast.makeText(
                                requireContext(),
                                if (isChecked) "Recruitment OPENED" else "Recruitment CLOSED",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Failed: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .addOnFailureListener {
                progressBar.visibility  = View.GONE
                switchRecruit.isEnabled = true
                tvStatus.text           = "Error loading"
            }
    }

    private fun updateRecruitStatusText(tvStatus: TextView, isOpen: Boolean) {
        tvStatus.text = if (isOpen) "Recruitment is OPEN" else "Recruitment is CLOSED"
        tvStatus.setTextColor(
            Color.parseColor(if (isOpen) "#2ECC71" else "#E74C3C")
        )
    }
}
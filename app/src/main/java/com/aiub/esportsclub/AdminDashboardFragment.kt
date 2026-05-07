package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class AdminDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAddEvent      = view.findViewById<Button>(R.id.btnAddEvent)
        val btnManageEvents  = view.findViewById<Button>(R.id.btnManageEvents)
        val btnAddPlayer     = view.findViewById<Button>(R.id.btnAddPlayer)
        val btnManagePlayers = view.findViewById<Button>(R.id.btnManagePlayers)
        val btnLogout        = view.findViewById<Button>(R.id.btnAdminLogout)

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

        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AdminLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        val btnCreateUpdate  = view.findViewById<Button>(R.id.btnCreateUpdate)
        val btnManageUpdates = view.findViewById<Button>(R.id.btnManageUpdates)

        btnCreateUpdate.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminCreateUpdateFragment())
        }

        btnManageUpdates.setOnClickListener {
            (requireActivity() as AdminActivity).loadFragment(AdminManageUpdatesFragment())
        }
    }
}
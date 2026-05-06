package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Find all buttons
        val btnAddEvent = findViewById<Button>(R.id.btnAddEvent)
        val btnManageEvents = findViewById<Button>(R.id.btnManageEvents)
        val btnAddPlayer = findViewById<Button>(R.id.btnAddPlayer)
        val btnManagePlayers = findViewById<Button>(R.id.btnManagePlayers)
        val btnLogout = findViewById<Button>(R.id.btnAdminLogout)

        // ===== NAVIGATE TO ADD EVENT =====
        btnAddEvent.setOnClickListener {
            val intent = Intent(this, AdminAddEventActivity::class.java)
            startActivity(intent)
        }

        // ===== NAVIGATE TO MANAGE EVENTS =====
        btnManageEvents.setOnClickListener {
            val intent = Intent(this, AdminManageEventsActivity::class.java)
            startActivity(intent)
        }

        // ===== NAVIGATE TO ADD PLAYER =====
        btnAddPlayer.setOnClickListener {
            val intent = Intent(this, AdminAddPlayerActivity::class.java)
            startActivity(intent)
        }

        // ===== NAVIGATE TO MANAGE PLAYERS =====
        btnManagePlayers.setOnClickListener {
            val intent = Intent(this, AdminManagePlayersActivity::class.java)
            startActivity(intent)
        }

        // ===== LOGOUT =====
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminLoginActivity::class.java)
            // Clear all screens — admin must login again
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
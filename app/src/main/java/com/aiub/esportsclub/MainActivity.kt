package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// MainActivity is the first screen that opens when the app starts
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEvents = findViewById<Button>(R.id.btnEvents)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnPlayers = findViewById<Button>(R.id.btnPlayers)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnViewRegistrations = findViewById<Button>(R.id.btnViewRegistrations)

        val auth = FirebaseAuth.getInstance()

        // When logout is clicked:
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        btnEvents.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnPlayers.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            startActivity(intent)
        }

        btnViewRegistrations.setOnClickListener {
            val intent = Intent(this, RegistrationsListActivity::class.java)
            startActivity(intent)
        }
    }
}

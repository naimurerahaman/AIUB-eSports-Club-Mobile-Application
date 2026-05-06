package com.aiub.esportsclub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class EventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // ===== BACK BUTTON =====
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // "finish()" closes the current screen and goes back
        }

        // ===== CREATE THE LIST OF EVENTS (Static Data) =====
        // We create a list manually. In a real app, this comes from a server.
        val eventList = listOf(
            Event(
                icon = "🔫",
                name = "PUBG Tournament",
                date = "July 15, 2025",
                prize = "BDT 10,000",
                description = "Join the ultimate PUBG battle royale tournament at AIUB! " +
                        "Teams of 4 will compete across 5 rounds. " +
                        "The last team standing wins the grand prize. " +
                        "Registration closes July 10. Venue: AIUB Lab 301."
            ),
            Event(
                icon = "🎯",
                name = "Valorant Tournament",
                date = "August 5, 2025",
                prize = "BDT 15,000",
                description = "Show your tactical skills in the AIUB Valorant Championship! " +
                        "5v5 format. Double elimination bracket. " +
                        "Top 3 teams win prizes and certificates. " +
                        "Registration closes August 1. Venue: AIUB Lab 405."
            ),
            Event(
                icon = "⚽",
                name = "FIFA Tournament",
                date = "August 20, 2025",
                prize = "BDT 5,000",
                description = "Prove you are the best FIFA player at AIUB! " +
                        "1v1 knockout rounds. " +
                        "Open to all AIUB students. " +
                        "Registration closes August 15. Venue: AIUB Lab 201."
            ),
            Event(
                icon = "📱",
                name = "Mobile Legends Tournament",
                date = "September 10, 2025",
                prize = "BDT 8,000",
                description = "5v5 Mobile Legends: Bang Bang tournament! " +
                        "Teams must register together. " +
                        "Winners get cash prizes and club merchandise. " +
                        "Registration closes September 5. Venue: AIUB Auditorium."
            )
        )

        // ===== SET UP THE RECYCLERVIEW =====
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEvents)

        // LinearLayoutManager arranges items in a straight list (top to bottom)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create the adapter and connect it to the RecyclerView
        val adapter = EventAdapter(this, eventList)
        recyclerView.adapter = adapter

    }
}
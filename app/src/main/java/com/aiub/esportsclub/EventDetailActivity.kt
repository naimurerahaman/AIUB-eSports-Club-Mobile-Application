package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // ===== RECEIVE THE DATA SENT FROM EventsActivity =====
        // Remember we used intent.putExtra() to send data?
        // Now we use intent.getStringExtra() to receive it.
        // The string inside "" must match EXACTLY what we used in putExtra()
        val eventName = intent.getStringExtra("event_name") ?: "Unknown Event"
        val eventDate = intent.getStringExtra("event_date") ?: "TBD"
        val eventPrize = intent.getStringExtra("event_prize") ?: "TBD"
        val eventDescription = intent.getStringExtra("event_description") ?: "No description."
        val eventIcon = intent.getStringExtra("event_icon") ?: "🎮"

        // ?: is the "Elvis operator" in Kotlin
        // It means: "if the value is null (empty), use this default value instead"

        // ===== DISPLAY THE DATA ON SCREEN =====
        findViewById<TextView>(R.id.tvDetailIcon).text = eventIcon
        findViewById<TextView>(R.id.tvDetailName).text = eventName
        findViewById<TextView>(R.id.tvDetailDate).text = eventDate
        findViewById<TextView>(R.id.tvDetailPrize).text = eventPrize
        findViewById<TextView>(R.id.tvDetailDescription).text = eventDescription

        // Back button
        findViewById<Button>(R.id.btnBackDetail).setOnClickListener {
            finish()
        }
    }
}
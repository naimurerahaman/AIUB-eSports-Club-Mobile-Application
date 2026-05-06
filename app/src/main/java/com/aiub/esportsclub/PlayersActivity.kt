package com.aiub.esportsclub

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlayersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        findViewById<Button>(R.id.btnBackPlayers).setOnClickListener {
            finish()
        }

        // Our static list of players
        val playerList = listOf(
            Player("Arafat Hossain", "PUBG Mobile", "🥇"),
            Player("Nadia Islam", "Valorant", "🥇"),
            Player("Rakib Hasan", "PUBG Mobile", "🥈"),
            Player("Sumaiya Akter", "Mobile Legends", "🥈"),
            Player("Mehedi Hassan", "FIFA 24", "🥈"),
            Player("Tanjim Ahmed", "Valorant", "🥉"),
            Player("Farhana Begum", "Free Fire", "🥉"),
            Player("Sabbir Rahman", "Call of Duty", "🥉"),
            Player("Rifat Karim", "Mobile Legends", "⭐"),
            Player("Lamia Zahan", "FIFA 24", "⭐")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPlayers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlayerAdapter(this, playerList)
    }
}
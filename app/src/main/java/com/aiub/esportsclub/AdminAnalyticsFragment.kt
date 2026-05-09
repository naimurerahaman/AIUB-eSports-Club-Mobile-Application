package com.aiub.esportsclub

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAnalyticsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    // We track how many fetch operations are running
    // When all finish, we hide the loading spinner
    // We have 6 operations total
    private var pendingCount = 6

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack    = view.findViewById<Button>(R.id.btnBackAnalytics)
        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshAnalytics)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Refresh button reloads all statistics from Firebase
        btnRefresh.setOnClickListener {
            loadAllStats(view)
        }

        // Load stats when fragment first opens
        loadAllStats(view)
    }

    // ===== MAIN FUNCTION — loads all stats =====
    private fun loadAllStats(view: View) {
        val progressBar    = view.findViewById<ProgressBar>(R.id.progressAnalytics)
        val tvLastUpdated  = view.findViewById<TextView>(R.id.tvLastUpdated)

        // Reset counter for this load
        pendingCount = 6

        // Show loading spinner
        progressBar.visibility = View.VISIBLE

        // Show all stat TextViews as loading
        view.findViewById<TextView>(R.id.tvTotalUsers).text          = "..."
        view.findViewById<TextView>(R.id.tvTotalRegistrations).text  = "..."
        view.findViewById<TextView>(R.id.tvTotalEvents).text         = "..."
        view.findViewById<TextView>(R.id.tvTotalPlayers).text        = "..."
        view.findViewById<TextView>(R.id.tvTotalUpdates).text        = "..."
        view.findViewById<TextView>(R.id.tvRegStatus).text           = "..."

        // ===== FETCH ALL STATS IN PARALLEL =====
        // Each fetch runs independently — they don't wait for each other
        // This makes the dashboard load faster
        fetchUserCount(view, progressBar, tvLastUpdated)
        fetchRegistrationCount(view, progressBar, tvLastUpdated)
        fetchEventCount(view, progressBar, tvLastUpdated)
        fetchPlayerCount(view, progressBar, tvLastUpdated)
        fetchUpdateCount(view, progressBar, tvLastUpdated)
        fetchRegistrationStatus(view, progressBar, tvLastUpdated)
        fetchPopularGames(view)
    }

    // ===== CALLED AFTER EACH FETCH COMPLETES =====
    // When ALL fetches finish, hide the spinner and show timestamp
    private fun onOneFetchComplete(
        progressBar   : ProgressBar,
        tvLastUpdated : TextView
    ) {
        pendingCount--
        if (pendingCount <= 0) {
            // All done — hide spinner
            progressBar.visibility = View.GONE

            // Show when data was last loaded
            val time = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(Date())
            tvLastUpdated.text = "Last updated at $time"
        }
    }

    // ===== FETCH 1: TOTAL USERS =====
    // We count documents in the "users" collection
    // Every time a user registers, we save their info there
    private fun fetchUserCount(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvUsers = view.findViewById<TextView>(R.id.tvTotalUsers)

        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // querySnapshot.size() = number of documents = number of users
                tvUsers.text = querySnapshot.size().toString()
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                // If "users" collection doesn't exist yet, show 0
                tvUsers.text = "0"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH 2: TOTAL REGISTRATIONS =====
    private fun fetchRegistrationCount(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvReg = view.findViewById<TextView>(R.id.tvTotalRegistrations)

        db.collection("registrations")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvReg.text = querySnapshot.size().toString()
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                tvReg.text = "0"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH 3: TOTAL EVENTS =====
    private fun fetchEventCount(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvEvents = view.findViewById<TextView>(R.id.tvTotalEvents)

        db.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvEvents.text = querySnapshot.size().toString()
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                tvEvents.text = "0"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH 4: TOTAL PLAYERS =====
    private fun fetchPlayerCount(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvPlayers = view.findViewById<TextView>(R.id.tvTotalPlayers)

        db.collection("players")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvPlayers.text = querySnapshot.size().toString()
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                tvPlayers.text = "0"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH 5: TOTAL UPDATES POSTED =====
    private fun fetchUpdateCount(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvUpdates = view.findViewById<TextView>(R.id.tvTotalUpdates)

        db.collection("updates")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tvUpdates.text = querySnapshot.size().toString()
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                tvUpdates.text = "0"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH 6: REGISTRATION STATUS =====
    private fun fetchRegistrationStatus(
        view         : View,
        progressBar  : ProgressBar,
        tvLastUpdated: TextView
    ) {
        val tvStatus = view.findViewById<TextView>(R.id.tvRegStatus)

        db.collection("settings")
            .document("registration")
            .get()
            .addOnSuccessListener { document ->
                val isOpen = document.getBoolean("isOpen") ?: false
                if (isOpen) {
                    tvStatus.text      = "OPEN"
                    tvStatus.setTextColor(Color.parseColor("#2ECC71")) // green
                } else {
                    tvStatus.text      = "CLOSED"
                    tvStatus.setTextColor(Color.parseColor("#E74C3C")) // red
                }
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
            .addOnFailureListener {
                tvStatus.text = "N/A"
                onOneFetchComplete(progressBar, tvLastUpdated)
            }
    }

    // ===== FETCH POPULAR GAMES =====
    // This reads all registrations and counts how many times
    // each game was selected
    private fun fetchPopularGames(view: View) {
        val layoutGameStats = view.findViewById<LinearLayout>(R.id.layoutGameStats)

        db.collection("registrations")
            .get()
            .addOnSuccessListener { querySnapshot ->

                // gameCount is a HashMap that tracks how many times each game appears
                // Key = game name, Value = count
                // Example: {"Valorant" -> 15, "PUBG" -> 8}
                val gameCount = mutableMapOf<String, Int>()

                // Loop through every registration document
                for (document in querySnapshot.documents) {

                    // "games" field is a List — e.g. ["Valorant", "PUBG"]
                    val games = document.get("games") as? List<*>
                    games?.forEach { game ->
                        val gameName = game.toString()
                        // If this game is already in the map, add 1
                        // If not, start with 1
                        gameCount[gameName] = (gameCount[gameName] ?: 0) + 1
                    }
                }

                // Sort by count — highest first
                // sortedByDescending returns a sorted list
                val sortedGames = gameCount.entries
                    .sortedByDescending { it.value }

                // Clear previous game rows
                layoutGameStats.removeAllViews()

                if (sortedGames.isEmpty()) {
                    // No registrations yet
                    val tvEmpty = TextView(requireContext())
                    tvEmpty.text      = "No registration data yet"
                    tvEmpty.textSize  = 13f
                    tvEmpty.setTextColor(Color.parseColor("#AAAAAA"))
                    layoutGameStats.addView(tvEmpty)
                    return@addOnSuccessListener
                }

                // Find the highest count for percentage calculation
                val maxCount = sortedGames.first().value

                // Create a row for each game
                sortedGames.forEach { (gameName, count) ->
                    addGameStatRow(layoutGameStats, gameName, count, maxCount)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error loading game stats: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ===== ADD ONE GAME ROW TO THE CHART =====
    private fun addGameStatRow(
        layout   : LinearLayout,
        gameName : String,
        count    : Int,
        maxCount : Int
    ) {
        // Create outer container for this game row
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 10
            layoutParams = params
        }

        // Game name + count label row
        val labelRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        // Game name text
        val tvName = TextView(requireContext()).apply {
            text     = gameName
            textSize = 13f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f  // takes remaining space
            )
        }

        // Count text
        val tvCount = TextView(requireContext()).apply {
            text     = "$count players"
            textSize = 12f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        labelRow.addView(tvName)
        labelRow.addView(tvCount)

        // ===== PROGRESS BAR (visual bar) =====
        // This creates a simple colored bar showing relative popularity
        val progressBar = android.widget.ProgressBar(
            requireContext(),
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max     = maxCount       // maximum value = highest count
            progress = count         // current value = this game's count
            progressTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor("#E94560") // accent color
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16  // height in pixels
            )
            params.topMargin = 4
            layoutParams = params
        }

        rowLayout.addView(labelRow)
        rowLayout.addView(progressBar)
        layout.addView(rowLayout)
    }
}
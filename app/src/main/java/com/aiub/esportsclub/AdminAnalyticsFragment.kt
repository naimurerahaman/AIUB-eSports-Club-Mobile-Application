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
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAnalyticsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

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

        btnRefresh.setOnClickListener {
            loadAllStats(view)
        }

        loadAllStats(view)
    }

    private fun loadAllStats(view: View) {
        val progressBar   = view.findViewById<ProgressBar>(R.id.progressAnalytics)
        val tvLastUpdated = view.findViewById<TextView>(R.id.tvLastUpdated)
        val layoutGames   = view.findViewById<LinearLayout>(R.id.layoutGameStats)

        // Show loading spinner
        progressBar.visibility = View.VISIBLE

        // Reset all cards to loading state
        view.findViewById<TextView>(R.id.tvTotalUsers).text         = "..."
        view.findViewById<TextView>(R.id.tvTotalRegistrations).text = "..."
        view.findViewById<TextView>(R.id.tvTotalEvents).text        = "..."
        view.findViewById<TextView>(R.id.tvTotalPlayers).text       = "..."
        view.findViewById<TextView>(R.id.tvTotalUpdates).text       = "..."
        view.findViewById<TextView>(R.id.tvRegStatus).text          = "..."

        // We use a simple counter to track completed fetches
        // Total fetches = 6
        var completed = 0
        val total     = 6

        // This function is called after EACH fetch finishes
        // When all 6 are done, hide the spinner
        fun onDone() {
            completed++
            if (completed >= total) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    tvLastUpdated.text = "Last updated at $time"
                }
            }
        }

        // ===== FETCH 1: USERS =====
        db.collection("users").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    view.findViewById<TextView>(R.id.tvTotalUsers).text =
                        if (task.isSuccessful) task.result?.size().toString() else "0"
                }
                onDone()
            }

        // ===== FETCH 2: REGISTRATIONS =====
        db.collection("registrations").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    view.findViewById<TextView>(R.id.tvTotalRegistrations).text =
                        if (task.isSuccessful) task.result?.size().toString() else "0"
                }
                onDone()
            }

        // ===== FETCH 3: EVENTS =====
        db.collection("events").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    view.findViewById<TextView>(R.id.tvTotalEvents).text =
                        if (task.isSuccessful) task.result?.size().toString() else "0"
                }
                onDone()
            }

        // ===== FETCH 4: APPROVED MEMBERS =====
        // We fetch ALL member applications then count approved ones locally
        // This avoids needing a Firestore composite index
        db.collection("memberApplications").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    var approvedCount = 0
                    if (task.isSuccessful) {
                        for (doc in task.result?.documents ?: emptyList()) {
                            if (doc.getString("status") == "approved") {
                                approvedCount++
                            }
                        }
                    }
                    view.findViewById<TextView>(R.id.tvTotalPlayers).text =
                        approvedCount.toString()
                }
                onDone()
            }

        // ===== FETCH 5: UPDATES =====
        db.collection("updates").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    view.findViewById<TextView>(R.id.tvTotalUpdates).text =
                        if (task.isSuccessful) task.result?.size().toString() else "0"
                }
                onDone()
            }

        // ===== FETCH 6: REGISTRATION STATUS =====
        db.collection("settings").document("registration").get()
            .addOnCompleteListener { task ->
                if (isAdded) {
                    val tvStatus = view.findViewById<TextView>(R.id.tvRegStatus)
                    val isOpen   = task.result?.getBoolean("isOpen") ?: false
                    tvStatus.text = if (isOpen) "OPEN" else "CLOSED"
                    tvStatus.setTextColor(
                        Color.parseColor(if (isOpen) "#2ECC71" else "#E74C3C")
                    )
                }
                onDone()
            }

        // ===== FETCH POPULAR GAMES =====
        // This is separate — does not affect the spinner counter
        db.collection("registrations").get()
            .addOnSuccessListener { querySnapshot ->
                if (!isAdded) return@addOnSuccessListener

                val gameCount = mutableMapOf<String, Int>()
                for (document in querySnapshot.documents) {
                    val games = document.get("games") as? List<*>
                    games?.forEach { game ->
                        val g = game.toString()
                        gameCount[g] = (gameCount[g] ?: 0) + 1
                    }
                }

                val sorted = gameCount.entries.sortedByDescending { it.value }
                layoutGames.removeAllViews()

                if (sorted.isEmpty()) {
                    val tv = TextView(requireContext())
                    tv.text      = "No registration data yet"
                    tv.textSize  = 13f
                    tv.setTextColor(Color.parseColor("#AAAAAA"))
                    layoutGames.addView(tv)
                    return@addOnSuccessListener
                }

                val maxCount = sorted.first().value
                sorted.forEach { (gameName, count) ->
                    addGameRow(layoutGames, gameName, count, maxCount)
                }
            }
    }

    private fun addGameRow(
        layout   : LinearLayout,
        gameName : String,
        count    : Int,
        maxCount : Int
    ) {
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 10 }
        }

        val labelRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val tvName = TextView(requireContext()).apply {
            text      = gameName
            textSize  = 13f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvCount = TextView(requireContext()).apply {
            text     = "$count players"
            textSize = 12f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        labelRow.addView(tvName)
        labelRow.addView(tvCount)

        val progressBar = android.widget.ProgressBar(
            requireContext(), null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max      = maxCount
            progress = count
            progressTintList = android.content.res.ColorStateList
                .valueOf(Color.parseColor("#E94560"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 16
            ).also { it.topMargin = 4 }
        }

        rowLayout.addView(labelRow)
        rowLayout.addView(progressBar)
        layout.addView(rowLayout)
    }
}
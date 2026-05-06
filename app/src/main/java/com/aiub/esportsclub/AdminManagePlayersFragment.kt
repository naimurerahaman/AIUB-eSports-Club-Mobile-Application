package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminManagePlayersFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminPlayerAdapter
    private val playerList = mutableListOf<AdminPlayer>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_manage_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackManagePlayers)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAdminPlayers)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create adapter WITH the onEditClick callback
        // When the Edit button is tapped, this lambda runs
        // It bundles the player data and opens AdminEditPlayerFragment
        adapter = AdminPlayerAdapter(requireContext(), playerList) { player ->

            val editFragment = AdminEditPlayerFragment()

            val bundle = Bundle()
            bundle.putString("documentId", player.documentId)
            bundle.putString("name",       player.name)
            bundle.putString("game",       player.game)
            bundle.putString("rank",       player.rank)
            bundle.putString("role",       player.role)

            editFragment.arguments = bundle

            // Navigate to edit fragment via AdminActivity's loadFragment()
            (requireActivity() as AdminActivity).loadFragment(editFragment)
        }

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        playerList.clear()
        adapter.notifyDataSetChanged()
        loadPlayers()
    }

    private fun loadPlayers() {
        val progressBar  = view?.findViewById<ProgressBar>(R.id.progressManagePlayers)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerAdminPlayers)
        val tvNoPlayers  = view?.findViewById<TextView>(R.id.tvNoPlayers)

        progressBar?.visibility  = View.VISIBLE
        recyclerView?.visibility = View.GONE

        db.collection("players")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->

                progressBar?.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvNoPlayers?.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    playerList.add(
                        AdminPlayer(
                            documentId = document.id,
                            name       = document.getString("name") ?: "",
                            game       = document.getString("game") ?: "",
                            rank       = document.getString("rank") ?: "",
                            role       = document.getString("role") ?: ""
                        )
                    )
                }

                recyclerView?.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
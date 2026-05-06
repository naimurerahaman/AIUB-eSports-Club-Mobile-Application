package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PlayersFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackPlayers)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewPlayers)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        db.collection("players")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()

                for (document in querySnapshot.documents) {
                    val player = Player(
                        name = document.getString("name") ?: "",
                        game = document.getString("game") ?: "",
                        rank = document.getString("rank") ?: "⭐"
                    )
                    playerList.add(player)
                }

                recyclerView.adapter = PlayerAdapter(requireContext(), playerList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
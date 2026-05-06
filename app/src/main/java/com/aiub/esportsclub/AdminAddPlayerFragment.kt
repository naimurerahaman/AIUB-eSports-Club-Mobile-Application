package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddPlayerFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_add_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack = view.findViewById<Button>(R.id.btnBackAddPlayer)
        val etName  = view.findViewById<EditText>(R.id.etPlayerName)
        val etGame  = view.findViewById<EditText>(R.id.etPlayerGame)
        val etRank  = view.findViewById<EditText>(R.id.etPlayerRank)
        val etRole  = view.findViewById<EditText>(R.id.etPlayerRole)
        val btnSave = view.findViewById<Button>(R.id.btnSavePlayer)

        btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val game = etGame.text.toString().trim()
            val rank = etRank.text.toString().trim()
            val role = etRole.text.toString().trim()

            if (name.isEmpty() || game.isEmpty() || rank.isEmpty() || role.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in ALL fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            db.collection("players")
                .add(hashMapOf("name" to name, "game" to game, "rank" to rank, "role" to role, "timestamp" to System.currentTimeMillis()))
                .addOnSuccessListener {
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Player to Firebase"
                    Toast.makeText(requireContext(), "Player added! ✅", Toast.LENGTH_SHORT).show()
                    etName.text.clear(); etGame.text.clear(); etRank.text.clear(); etRole.text.clear()
                }
                .addOnFailureListener { e ->
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Player to Firebase"
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
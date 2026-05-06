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

class AdminEditPlayerFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var documentId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_edit_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack   = view.findViewById<Button>(R.id.btnBackEditPlayer)
        val etName    = view.findViewById<EditText>(R.id.etEditPlayerName)
        val etGame    = view.findViewById<EditText>(R.id.etEditPlayerGame)
        val etRank    = view.findViewById<EditText>(R.id.etEditPlayerRank)
        val etRole    = view.findViewById<EditText>(R.id.etEditPlayerRole)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdatePlayer)

        btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        documentId = arguments?.getString("documentId") ?: ""
        etName.setText(arguments?.getString("name") ?: "")
        etGame.setText(arguments?.getString("game") ?: "")
        etRank.setText(arguments?.getString("rank") ?: "")
        etRole.setText(arguments?.getString("role") ?: "")

        btnUpdate.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newGame = etGame.text.toString().trim()
            val newRank = etRank.text.toString().trim()
            val newRole = etRole.text.toString().trim()

            if (newName.isEmpty() || newGame.isEmpty() || newRank.isEmpty() || newRole.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."

            db.collection("players")
                .document(documentId)
                .update(mapOf("name" to newName, "game" to newGame, "rank" to newRank, "role" to newRole))
                .addOnSuccessListener {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Player"
                    Toast.makeText(requireContext(), "Player updated! ✅", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Player"
                    Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
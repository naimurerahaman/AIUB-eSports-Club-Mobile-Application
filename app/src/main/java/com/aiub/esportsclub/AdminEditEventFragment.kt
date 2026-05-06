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

class AdminEditEventFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var documentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_edit_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack = view.findViewById<Button>(R.id.btnBackEdit)
        val etName  = view.findViewById<EditText>(R.id.etEditEventName)
        val etGame  = view.findViewById<EditText>(R.id.etEditGameName)
        val etDate  = view.findViewById<EditText>(R.id.etEditEventDate)
        val etPrize = view.findViewById<EditText>(R.id.etEditEventPrize)
        val etDesc  = view.findViewById<EditText>(R.id.etEditEventDescription)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdateEvent)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Read data from Bundle (sent from AdminManageEventsFragment)
        documentId = arguments?.getString("documentId") ?: ""

        // Pre-fill fields with existing data
        etName.setText(arguments?.getString("name")        ?: "")
        etGame.setText(arguments?.getString("game")        ?: "")
        etDate.setText(arguments?.getString("date")        ?: "")
        etPrize.setText(arguments?.getString("prize")      ?: "")
        etDesc.setText(arguments?.getString("description") ?: "")

        btnUpdate.setOnClickListener {
            val newName  = etName.text.toString().trim()
            val newGame  = etGame.text.toString().trim()
            val newDate  = etDate.text.toString().trim()
            val newPrize = etPrize.text.toString().trim()
            val newDesc  = etDesc.text.toString().trim()

            if (newName.isEmpty() || newGame.isEmpty() || newDate.isEmpty() ||
                newPrize.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."

            db.collection("events")
                .document(documentId)
                .update(mapOf(
                    "name"        to newName,
                    "game"        to newGame,
                    "date"        to newDate,
                    "prize"       to newPrize,
                    "description" to newDesc
                ))
                .addOnSuccessListener {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Event"
                    Toast.makeText(requireContext(), "Event updated! ✅", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { exception ->
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Event"
                    Toast.makeText(requireContext(), "Update failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
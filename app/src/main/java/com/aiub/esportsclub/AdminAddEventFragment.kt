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

class AdminAddEventFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack = view.findViewById<Button>(R.id.btnBackAddEvent)
        val etName  = view.findViewById<EditText>(R.id.etEventName)
        val etGame  = view.findViewById<EditText>(R.id.etGameName)
        val etDate  = view.findViewById<EditText>(R.id.etEventDate)
        val etPrize = view.findViewById<EditText>(R.id.etEventPrize)
        val etDesc  = view.findViewById<EditText>(R.id.etEventDescription)
        val btnSave = view.findViewById<Button>(R.id.btnSaveEvent)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            val name  = etName.text.toString().trim()
            val game  = etGame.text.toString().trim()
            val date  = etDate.text.toString().trim()
            val prize = etPrize.text.toString().trim()
            val desc  = etDesc.text.toString().trim()

            if (name.isEmpty() || game.isEmpty() || date.isEmpty() || prize.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in ALL fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val eventData = hashMapOf(
                "name"        to name,
                "game"        to game,
                "date"        to date,
                "prize"       to prize,
                "description" to desc,
                "timestamp"   to System.currentTimeMillis()
            )

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            db.collection("events")
                .add(eventData)
                .addOnSuccessListener {
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Event to Firebase"
                    Toast.makeText(requireContext(), "Event saved! ✅", Toast.LENGTH_SHORT).show()
                    etName.text.clear(); etGame.text.clear()
                    etDate.text.clear(); etPrize.text.clear(); etDesc.text.clear()
                }
                .addOnFailureListener { exception ->
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Event to Firebase"
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
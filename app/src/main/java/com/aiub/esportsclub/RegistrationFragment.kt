package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnBack       = view.findViewById<Button>(R.id.btnBackReg)
        val etName        = view.findViewById<EditText>(R.id.etName)
        val etStudentId   = view.findViewById<EditText>(R.id.etStudentId)
        val etTeamName    = view.findViewById<EditText>(R.id.etTeamName)
        val btnSubmit     = view.findViewById<Button>(R.id.btnSubmit)
        val layoutSuccess = view.findViewById<LinearLayout>(R.id.layoutSuccess)
        val tvSuccess     = view.findViewById<TextView>(R.id.tvSuccessMessage)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSubmit.setOnClickListener {
            val name      = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val teamName  = etTeamName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studentId.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your Student ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (teamName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your team name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registrationData = hashMapOf(
                "name"      to name,
                "studentId" to studentId,
                "teamName"  to teamName,
                "userId"    to (auth.currentUser?.uid   ?: "unknown"),
                "userEmail" to (auth.currentUser?.email ?: "unknown"),
                "timestamp" to System.currentTimeMillis()
            )

            btnSubmit.isEnabled = false
            btnSubmit.text = "Submitting..."

            db.collection("registrations")
                .add(registrationData)
                .addOnSuccessListener {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "✅ Submit Registration"

                    tvSuccess.text = "Name: $name\nStudent ID: $studentId\nTeam: $teamName\n\nSaved online! ☁️"
                    layoutSuccess.visibility = View.VISIBLE
                    btnSubmit.visibility = View.GONE

                    Toast.makeText(requireContext(), "Registration saved! ✅", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "✅ Submit Registration"
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
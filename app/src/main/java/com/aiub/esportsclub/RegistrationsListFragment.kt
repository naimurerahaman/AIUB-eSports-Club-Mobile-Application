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

class RegistrationsListFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registrations_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackRegList)
        val progressBar  = view.findViewById<ProgressBar>(R.id.progressBarList)
        val tvEmpty      = view.findViewById<TextView>(R.id.tvEmptyMessage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRegistrations)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        db.collection("registrations")
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvEmpty.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val list = mutableListOf<Registration>()
                for (document in querySnapshot.documents) {
                    list.add(Registration(
                        name      = document.getString("name")      ?: "",
                        studentId = document.getString("studentId") ?: "",
                        teamName  = document.getString("teamName")  ?: ""
                    ))
                }

                recyclerView.visibility = View.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = RegistrationAdapter(requireContext(), list)
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
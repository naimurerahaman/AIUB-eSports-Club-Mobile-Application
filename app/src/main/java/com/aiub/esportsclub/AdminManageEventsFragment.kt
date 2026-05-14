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

class AdminManageEventsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminEventAdapter
    private val eventList = mutableListOf<AdminEvent>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_manage_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackManageEvents)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAdminEvents)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminEventAdapter(requireContext(), eventList) { event ->
            val editFragment = AdminEditEventFragment()

            val bundle = Bundle()
            bundle.putString("documentId",    event.documentId)
            bundle.putString("name",          event.name)
            bundle.putString("game",          event.game)
            bundle.putString("date",          event.date)
            bundle.putString("prize",         event.prize)
            bundle.putString("description",   event.description)
            bundle.putString("bannerImageUrl", event.bannerImageUrl) // ← ADDED

            editFragment.arguments = bundle
            (requireActivity() as AdminActivity).loadFragment(editFragment)
        }

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        eventList.clear()
        adapter.notifyDataSetChanged()
        loadEvents()
    }

    private fun loadEvents() {
        val progressBar  = view?.findViewById<ProgressBar>(R.id.progressManageEvents)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerAdminEvents)
        val tvNoEvents   = view?.findViewById<TextView>(R.id.tvNoEvents)

        progressBar?.visibility  = View.VISIBLE
        recyclerView?.visibility = View.GONE

        db.collection("events")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar?.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvNoEvents?.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    eventList.add(AdminEvent(
                        documentId     = document.id,
                        name           = document.getString("name")           ?: "",
                        game           = document.getString("game")           ?: "",
                        date           = document.getString("date")           ?: "",
                        prize          = document.getString("prize")          ?: "",
                        description    = document.getString("description")    ?: "",
                        bannerImageUrl = document.getString("bannerImageUrl") ?: "" // ← ADDED
                    ))
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
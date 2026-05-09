package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EventsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBack)
        val progressBar = view.findViewById<ProgressBar?>(R.id.progressBarEvents)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEvents)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        db.collection("events")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->

                progressBar?.visibility = View.GONE

                val eventList = mutableListOf<Event>()

                for (document in querySnapshot.documents) {
                    val event = Event(
                        documentId       = document.id,
                        icon             = document.getString("icon")             ?: "🎮",
                        name             = document.getString("name")             ?: "",
                        date             = document.getString("date")             ?: "",
                        prize            = document.getString("prize")            ?: "",
                        description      = document.getString("description")     ?: "",
                        game             = document.getString("game")             ?: "",
                        registrationLink = document.getString("registrationLink") ?: "",
                        eventLink        = document.getString("eventLink")        ?: "",
                        bannerImageUrl   = document.getString("bannerImageUrl")   ?: ""
                    )
                    eventList.add(event)
                }

                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                // ✅ EventAdapter now takes a 3rd parameter — the click callback
                val adapter = EventAdapter(requireContext(), eventList) { clickedEvent ->

                    // When "View Details" is tapped, create and open EventDetailFragment
                    val detailFragment = EventDetailFragment()

                    val bundle = Bundle()
                    bundle.putString("event_name",        clickedEvent.name)
                    bundle.putString("event_date",        clickedEvent.date)
                    bundle.putString("event_prize",       clickedEvent.prize)
                    bundle.putString("event_description", clickedEvent.description)
                    bundle.putString("event_icon",        clickedEvent.icon)

                    detailFragment.arguments = bundle

                    (requireActivity() as MainActivity).loadFragment(detailFragment)
                }

                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
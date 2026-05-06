package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class EventDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<Button>(R.id.btnBackDetail)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== READ DATA FROM ARGUMENTS (Bundle) =====
        // arguments is the Bundle we attached in EventsFragment
        // This is the Fragment equivalent of intent.getStringExtra()
        val eventName  = arguments?.getString("event_name")        ?: "Unknown Event"
        val eventDate  = arguments?.getString("event_date")        ?: "TBD"
        val eventPrize = arguments?.getString("event_prize")       ?: "TBD"
        val eventDesc  = arguments?.getString("event_description") ?: "No description."
        val eventIcon  = arguments?.getString("event_icon")        ?: "🎮"

        // Display data on screen
        view.findViewById<TextView>(R.id.tvDetailIcon).text        = eventIcon
        view.findViewById<TextView>(R.id.tvDetailName).text        = eventName
        view.findViewById<TextView>(R.id.tvDetailDate).text        = eventDate
        view.findViewById<TextView>(R.id.tvDetailPrize).text       = eventPrize
        view.findViewById<TextView>(R.id.tvDetailDescription).text = eventDesc
    }
}
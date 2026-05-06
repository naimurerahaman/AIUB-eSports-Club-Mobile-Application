package com.aiub.esportsclub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Added "onDetailsClick" — a callback function
// When the button is clicked, the adapter calls this function
// The Fragment decides what to do (navigate to EventDetailFragment)
class EventAdapter(
    private val context: Context,
    private val eventList: List<Event>,
    private val onDetailsClick: (Event) -> Unit  // NEW: callback lambda
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon    : TextView = itemView.findViewById(R.id.tvEventIcon)
        val tvName    : TextView = itemView.findViewById(R.id.tvEventName)
        val tvDate    : TextView = itemView.findViewById(R.id.tvEventDate)
        val tvPrize   : TextView = itemView.findViewById(R.id.tvEventPrize)
        val btnDetails: Button   = itemView.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        holder.tvIcon.text  = event.icon
        holder.tvName.text  = event.name
        holder.tvDate.text  = "📆 Date: ${event.date}"
        holder.tvPrize.text = "🏆 Prize: ${event.prize}"

        holder.btnDetails.setOnClickListener {
            // Call the callback — pass the clicked event to the Fragment
            // The Fragment will handle the navigation
            onDetailsClick(event)
        }
    }

    override fun getItemCount(): Int = eventList.size
}
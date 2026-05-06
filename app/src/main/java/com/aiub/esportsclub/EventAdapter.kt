package com.aiub.esportsclub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// EventAdapter connects our list of Event objects to the RecyclerView
// It takes a Context (the current screen) and a list of Event objects
class EventAdapter(
    private val context: Context,
    private val eventList: List<Event>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder holds references to the views in ONE card
    // This prevents Android from searching for views repeatedly (faster app)
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find all the views inside item_event.xml
        val tvIcon: TextView = itemView.findViewById(R.id.tvEventIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvEventName)
        val tvDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvPrize: TextView = itemView.findViewById(R.id.tvEventPrize)
        val btnDetails: Button = itemView.findViewById(R.id.btnViewDetails)
    }

    // This function is called when a new card view needs to be created
    // It "inflates" (builds) the card from item_event.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    // This function is called to fill one card with data
    // "position" is the index — 0 = first item, 1 = second, etc.
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // Get the event at this position from the list
        val event = eventList[position]

        // Fill each view in the card with data from the event object
        holder.tvIcon.text = event.icon
        holder.tvName.text = event.name
        holder.tvDate.text = "📆 Date: ${event.date}"
        holder.tvPrize.text = "🏆 Prize: ${event.prize}"

        // When "View Details" button is clicked, open the detail screen
        holder.btnDetails.setOnClickListener {
            val intent = Intent(context, EventDetailActivity::class.java)
            // putExtra sends extra data to the next screen
            // We're sending the event name and description as text
            intent.putExtra("event_name", event.name)
            intent.putExtra("event_date", event.date)
            intent.putExtra("event_prize", event.prize)
            intent.putExtra("event_description", event.description)
            intent.putExtra("event_icon", event.icon)
            context.startActivity(intent)
        }
    }

    // This tells RecyclerView how many items are in the list
    override fun getItemCount(): Int {
        return eventList.size
    }

} // End of EventAdapter
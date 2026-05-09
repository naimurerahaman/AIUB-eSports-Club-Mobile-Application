package com.aiub.esportsclub

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventAdapter(
    private val context       : Context,
    private val eventList     : List<Event>,
    private val onDetailsClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner       : ImageView = itemView.findViewById(R.id.ivEventBanner)
        val tvIcon         : TextView  = itemView.findViewById(R.id.tvEventIcon)
        val tvName         : TextView  = itemView.findViewById(R.id.tvEventName)
        val tvGame         : TextView  = itemView.findViewById(R.id.tvEventGame)
        val tvDate         : TextView  = itemView.findViewById(R.id.tvEventDate)
        val tvPrize        : TextView  = itemView.findViewById(R.id.tvEventPrize)
        val tvDescription  : TextView  = itemView.findViewById(R.id.tvEventDescription)
        val btnRegister    : Button    = itemView.findViewById(R.id.btnEventRegister)
        val btnEventLink   : Button    = itemView.findViewById(R.id.btnEventLink)
        val btnViewDetails : Button    = itemView.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        // ===== FILL BASIC INFO =====
        holder.tvIcon.text        = event.icon
        holder.tvName.text        = event.name
        holder.tvGame.text        = event.game
        holder.tvDate.text        = "Date: ${event.date}"
        holder.tvPrize.text       = "Prize: ${event.prize}"
        holder.tvDescription.text = event.description

        // ===== LOAD BANNER IMAGE =====
        if (event.bannerImageUrl.isNotEmpty()) {
            holder.ivBanner.visibility = View.VISIBLE

            // Glide downloads the image from the URL and puts it in the ImageView
            // .placeholder() shows a gray color while loading
            // .error() shows a fallback if loading fails
            // .centerCrop() fills the ImageView without stretching
            Glide.with(context)
                .load(event.bannerImageUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.ivBanner)
        } else {
            holder.ivBanner.visibility = View.GONE
        }

        // ===== REGISTRATION LINK BUTTON =====
        if (event.registrationLink.isNotEmpty()) {
            holder.btnRegister.visibility = View.VISIBLE
            holder.btnRegister.setOnClickListener {
                openLink(event.registrationLink)
            }
        } else {
            holder.btnRegister.visibility = View.GONE
        }

        // ===== EVENT LINK BUTTON =====
        if (event.eventLink.isNotEmpty()) {
            holder.btnEventLink.visibility = View.VISIBLE
            holder.btnEventLink.setOnClickListener {
                openLink(event.eventLink)
            }
        } else {
            holder.btnEventLink.visibility = View.GONE
        }

        // ===== VIEW DETAILS BUTTON =====
        // Only show if both link buttons are hidden
        if (event.registrationLink.isEmpty() && event.eventLink.isEmpty()) {
            holder.btnViewDetails.visibility = View.VISIBLE
            holder.btnViewDetails.setOnClickListener {
                onDetailsClick(event)
            }
        } else {
            holder.btnViewDetails.visibility = View.GONE
        }
    }

    // ===== OPEN LINK IN BROWSER =====
    private fun openLink(url: String) {
        try {
            var finalUrl = url.trim()

            // Make sure URL starts with http or https
            // Without this the browser won't recognize it as a web link
            if (!finalUrl.startsWith("http://") &&
                !finalUrl.startsWith("https://")) {
                finalUrl = "https://$finalUrl"
            }

            // Intent.ACTION_VIEW tells Android to VIEW something
            // Uri.parse() converts the string URL to a Uri object
            // The system automatically opens it in the browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Cannot open link. Please check the URL.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = eventList.size
}
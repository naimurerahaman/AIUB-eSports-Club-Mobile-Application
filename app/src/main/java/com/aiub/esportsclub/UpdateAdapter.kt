package com.aiub.esportsclub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// UpdateAdapter connects our list of Update objects to the RecyclerView
// Think of it as a waiter:
// Kitchen (Firebase data) → Waiter (Adapter) → Table (RecyclerView on screen)
class UpdateAdapter(
    private val context    : Context,
    private val updateList : List<Update>
) : RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder>() {

    // ===== VIEW HOLDER =====
    // Stores references to all views inside ONE card
    // Prevents Android from searching for views repeatedly
    class UpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle      : TextView  = itemView.findViewById(R.id.tvUpdateTitle)
        val tvDescription: TextView  = itemView.findViewById(R.id.tvUpdateDescription)
        val tvDate       : TextView  = itemView.findViewById(R.id.tvUpdateDate)
        val ivImage      : ImageView = itemView.findViewById(R.id.ivUpdateImage)
    }

    // Called when RecyclerView needs a new card to display
    // Inflates (builds) the card from item_update.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_update, parent, false)
        return UpdateViewHolder(view)
    }

    // Called to fill ONE card with data
    // "position" = index of the item (0 = first, 1 = second, etc.)
    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        val update = updateList[position]

        // Fill text fields with data
        holder.tvTitle.text       = update.title
        holder.tvDescription.text = update.description

        // ===== FORMAT THE TIMESTAMP =====
        // timestamp is stored as milliseconds (a big number like 1720000000000)
        // We convert it to a readable date like "Jul 15, 2025 at 3:45 PM"
        if (update.timestamp != 0L) {
            val date       = Date(update.timestamp)
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            holder.tvDate.text = "🕐 ${dateFormat.format(date)}"
        } else {
            holder.tvDate.text = "🕐 Just now"
        }

        // ===== HANDLE IMAGE =====
        // If imageUrl is not empty, show the image
        // If imageUrl is empty, keep the ImageView hidden
        if (update.imageUrl.isNotEmpty()) {
            // Make the ImageView visible
            holder.ivImage.visibility = View.VISIBLE

            // Glide loads the image from the URL into the ImageView
            // .placeholder() shows a color while image is loading
            // .error() shows a fallback if image fails to load
            Glide.with(context)
                .load(update.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.ivImage)
        } else {
            // No image URL — hide the ImageView completely
            holder.ivImage.visibility = View.GONE
        }
    }

    // Tells RecyclerView how many items are in our list
    override fun getItemCount(): Int = updateList.size
}
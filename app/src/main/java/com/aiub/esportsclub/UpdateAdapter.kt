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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateAdapter(
    private val context    : Context,
    private val updateList : List<Update>
) : RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder>() {

    class UpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle      : TextView  = itemView.findViewById(R.id.tvUpdateTitle)
        val tvDescription: TextView  = itemView.findViewById(R.id.tvUpdateDescription)
        val tvDate       : TextView  = itemView.findViewById(R.id.tvUpdateDate)
        val ivImage      : ImageView = itemView.findViewById(R.id.ivUpdateImage)
        val btnLink      : Button    = itemView.findViewById(R.id.btnUpdateLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_update, parent, false)
        return UpdateViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        val update = updateList[position]

        // Fill text fields
        holder.tvTitle.text       = update.title
        holder.tvDescription.text = update.description

        // Format timestamp
        if (update.timestamp != 0L) {
            val date       = Date(update.timestamp)
            val dateFormat = SimpleDateFormat(
                "MMM dd, yyyy 'at' hh:mm a",
                Locale.getDefault()
            )
            holder.tvDate.text = "🕐 ${dateFormat.format(date)}"
        } else {
            holder.tvDate.text = "🕐 Just now"
        }

        // ===== HANDLE IMAGE =====
        if (update.imageUrl.isNotEmpty()) {
            holder.ivImage.visibility = View.VISIBLE

            Glide.with(context)
                .load(update.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.ivImage)
        } else {
            holder.ivImage.visibility = View.GONE
        }

        // ===== HANDLE LINK BUTTON =====
        if (update.link.isNotEmpty()) {
            holder.btnLink.visibility = View.VISIBLE

            holder.btnLink.setOnClickListener {
                try {
                    var url = update.link

                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://$url"
                    }

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Could not open link. Please check the URL.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            holder.btnLink.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = updateList.size
}
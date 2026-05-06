package com.aiub.esportsclub

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminEventAdapter(
    private val context: Context,
    private val eventList: MutableList<AdminEvent>,
    private val onEditClick: (AdminEvent) -> Unit  // NEW: callback for edit
) : RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class AdminEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName   : TextView = itemView.findViewById(R.id.tvAdminEventName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvAdminEventDetails)
        val btnEdit  : Button   = itemView.findViewById(R.id.btnEditEvent)
        val btnDelete: Button   = itemView.findViewById(R.id.btnDeleteEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminEventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_event, parent, false)
        return AdminEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminEventViewHolder, position: Int) {
        val event = eventList[position]

        holder.tvName.text    = event.name
        holder.tvDetails.text = "🎮 ${event.game}  |  📆 ${event.date}  |  🏆 ${event.prize}"

        // Edit button calls the callback — Fragment handles navigation
        holder.btnEdit.setOnClickListener {
            onEditClick(event)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Delete '${event.name}'? This cannot be undone.")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    deleteEvent(event, position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun deleteEvent(event: AdminEvent, position: Int) {
        db.collection("events")
            .document(event.documentId)
            .delete()
            .addOnSuccessListener {
                eventList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, eventList.size)
                Toast.makeText(context, "Event deleted ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Delete failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun getItemCount(): Int = eventList.size
}
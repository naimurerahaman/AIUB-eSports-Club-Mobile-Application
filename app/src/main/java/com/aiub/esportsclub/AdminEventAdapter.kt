package com.aiub.esportsclub

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// AdminEvent holds the data for one event, INCLUDING its Firestore document ID
// The document ID is critical — without it we cannot edit or delete the right document
data class AdminEvent(
    val documentId: String,  // Unique ID given by Firebase — like a receipt number
    val name: String,
    val game: String,
    val date: String,
    val prize: String,
    val description: String
)

class AdminEventAdapter(
    private val context: Context,
    // MutableList means we CAN add/remove items from this list
    // (unlike a regular List which is fixed)
    private val eventList: MutableList<AdminEvent>
) : RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder>() {

    // Connect to Firestore
    private val db = FirebaseFirestore.getInstance()

    class AdminEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAdminEventName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvAdminEventDetails)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditEvent)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminEventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_event, parent, false)
        return AdminEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminEventViewHolder, position: Int) {
        val event = eventList[position]

        // Fill the card with data
        holder.tvName.text = event.name
        holder.tvDetails.text = "🎮 ${event.game}  |  📆 ${event.date}  |  🏆 ${event.prize}"

        // ===== EDIT BUTTON =====
        holder.btnEdit.setOnClickListener {
            // Open the Edit screen and SEND the event data to it
            val intent = Intent(context, AdminEditEventActivity::class.java)

            // We send the document ID so the edit screen knows WHICH document to update
            intent.putExtra("documentId", event.documentId)
            intent.putExtra("name", event.name)
            intent.putExtra("game", event.game)
            intent.putExtra("date", event.date)
            intent.putExtra("prize", event.prize)
            intent.putExtra("description", event.description)

            context.startActivity(intent)
        }

        // ===== DELETE BUTTON =====
        holder.btnDelete.setOnClickListener {

            // Show a confirmation dialog BEFORE deleting
            // This prevents accidental deletion
            AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete '${event.name}'? This cannot be undone.")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    // Admin confirmed — proceed with deletion
                    deleteEvent(event, position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Admin changed their mind — do nothing
                    dialog.dismiss()
                }
                .show()
        }
    }

    // ===== DELETE FUNCTION =====
    private fun deleteEvent(event: AdminEvent, position: Int) {

        // db.collection("events") — go to the events collection
        // .document(event.documentId) — find the SPECIFIC document by its ID
        // .delete() — remove it permanently
        db.collection("events")
            .document(event.documentId)
            .delete()
            .addOnSuccessListener {
                // Remove from our local list too (so the screen updates immediately)
                eventList.removeAt(position)
                // Tell the RecyclerView that an item was removed at this position
                notifyItemRemoved(position)
                // Also update positions of remaining items
                notifyItemRangeChanged(position, eventList.size)

                Toast.makeText(context, "Event deleted ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Delete failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun getItemCount(): Int = eventList.size
}
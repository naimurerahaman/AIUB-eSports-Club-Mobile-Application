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

// ===== DATA CLASS FOR ONE ADMIN PLAYER =====
// This holds all the information for one player
// INCLUDING the documentId — this is the unique ID from Firebase
// Without documentId, we cannot edit or delete the correct player
data class AdminPlayer(
    val documentId: String,  // Firebase auto-generated ID e.g. "xK9mP2qrT1..."
    val name: String,
    val game: String,
    val rank: String,
    val role: String
)

// ===== ADAPTER CLASS =====
// The Adapter is the bridge between our list of AdminPlayer objects
// and the RecyclerView that displays them on screen
//
// Think of it like a waiter:
// Kitchen (data list) → Waiter (Adapter) → Table (RecyclerView on screen)
class AdminPlayerAdapter(
    private val context: Context,
    // MutableList means we CAN add/remove items
    // Regular List does not allow changes
    private val playerList: MutableList<AdminPlayer>
) : RecyclerView.Adapter<AdminPlayerAdapter.PlayerViewHolder>() {

    // Connect to Firestore inside the adapter
    // We need this for the delete operation
    private val db = FirebaseFirestore.getInstance()

    // ===== VIEW HOLDER =====
    // ViewHolder stores references to all the views inside ONE card
    // This avoids Android having to search for views repeatedly (saves memory)
    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar  : TextView = itemView.findViewById(R.id.tvAdminPlayerAvatar)
        val tvName    : TextView = itemView.findViewById(R.id.tvAdminPlayerName)
        val tvRank    : TextView = itemView.findViewById(R.id.tvAdminPlayerRank)
        val tvDetails : TextView = itemView.findViewById(R.id.tvAdminPlayerDetails)
        val btnEdit   : Button   = itemView.findViewById(R.id.btnEditPlayer)
        val btnDelete : Button   = itemView.findViewById(R.id.btnDeletePlayer)
    }

    // ===== onCreateViewHolder =====
    // Called when RecyclerView needs a NEW card view to be built
    // "inflate" means: build/expand the card from the XML layout file
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_admin_player,  // Use our card layout
            parent,
            false
        )
        return PlayerViewHolder(view)
    }

    // ===== onBindViewHolder =====
    // Called to fill ONE card with data from the list
    // "position" is the index: 0 = first card, 1 = second card, etc.
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {

        // Get the player at this position from our list
        val player = playerList[position]

        // ===== FILL THE CARD VIEWS WITH DATA =====

        // Show the first letter of the player's name as the avatar
        // .first() gets the first character, .toString() converts it to a String
        holder.tvAvatar.text  = player.name.first().toString()
        holder.tvName.text    = player.name
        holder.tvRank.text    = player.rank
        holder.tvDetails.text = "🎮 ${player.game}  |  ⚔️ ${player.role}"

        // ===== EDIT BUTTON =====
        holder.btnEdit.setOnClickListener {

            // Open the Edit screen
            val intent = Intent(context, AdminEditPlayerActivity::class.java)

            // Send ALL the player's data to the edit screen
            // The edit screen needs this to pre-fill the form fields
            // "documentId" is the most important — it tells Firebase WHICH player to update
            intent.putExtra("documentId", player.documentId)
            intent.putExtra("name",       player.name)
            intent.putExtra("game",       player.game)
            intent.putExtra("rank",       player.rank)
            intent.putExtra("role",       player.role)

            context.startActivity(intent)
        }

        // ===== DELETE BUTTON =====
        holder.btnDelete.setOnClickListener {

            // Show a confirmation popup BEFORE deleting
            // This prevents the admin from accidentally deleting a player
            AlertDialog.Builder(context)
                .setTitle("Delete Player")
                .setMessage("Are you sure you want to delete '${player.name}'?\nThis cannot be undone.")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    // Admin confirmed — go ahead and delete
                    deletePlayer(player, position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Admin changed their mind — do nothing, just close the dialog
                    dialog.dismiss()
                }
                .show()
        }
    }

    // ===== DELETE FUNCTION =====
    // This function is called when admin confirms deletion
    private fun deletePlayer(player: AdminPlayer, position: Int) {

        // db.collection("players")           → go to the "players" collection
        // .document(player.documentId)       → find this SPECIFIC document by its ID
        // .delete()                          → permanently remove it from Firestore
        db.collection("players")
            .document(player.documentId)
            .delete()
            .addOnSuccessListener {
                // Deletion from Firebase was successful

                // Now also remove it from our LOCAL list
                // so the screen updates instantly without needing to refresh
                playerList.removeAt(position)

                // Tell the RecyclerView: "one item was removed at this position"
                // This triggers the smooth removal animation
                notifyItemRemoved(position)

                // Update the position numbers of all items after the deleted one
                notifyItemRangeChanged(position, playerList.size)

                Toast.makeText(context, "${player.name} deleted ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Delete failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ===== getItemCount =====
    // Tells RecyclerView how many items are in the list
    // RecyclerView calls this to know when to stop creating cards
    override fun getItemCount(): Int = playerList.size

} // End of AdminPlayerAdapter
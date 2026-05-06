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

class AdminPlayerAdapter(
    private val context: Context,
    private val playerList: MutableList<AdminPlayer>,
    // This is the callback — the Fragment passes a function here
    // When Edit is clicked, the adapter calls this function and
    // passes the player object. The Fragment then handles navigation.
    private val onEditClick: (AdminPlayer) -> Unit
) : RecyclerView.Adapter<AdminPlayerAdapter.PlayerViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar  : TextView = itemView.findViewById(R.id.tvAdminPlayerAvatar)
        val tvName    : TextView = itemView.findViewById(R.id.tvAdminPlayerName)
        val tvRank    : TextView = itemView.findViewById(R.id.tvAdminPlayerRank)
        val tvDetails : TextView = itemView.findViewById(R.id.tvAdminPlayerDetails)
        val btnEdit   : Button   = itemView.findViewById(R.id.btnEditPlayer)
        val btnDelete : Button   = itemView.findViewById(R.id.btnDeletePlayer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playerList[position]

        holder.tvAvatar.text  = player.name.first().toString()
        holder.tvName.text    = player.name
        holder.tvRank.text    = player.rank
        holder.tvDetails.text = "🎮 ${player.game}  |  ⚔️ ${player.role}"

        // Call the callback — Fragment decides where to navigate
        holder.btnEdit.setOnClickListener {
            onEditClick(player)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Player")
                .setMessage("Delete '${player.name}'? This cannot be undone.")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    deletePlayer(player, position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun deletePlayer(player: AdminPlayer, position: Int) {
        db.collection("players")
            .document(player.documentId)
            .delete()
            .addOnSuccessListener {
                playerList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, playerList.size)
                Toast.makeText(context, "${player.name} deleted ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Delete failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun getItemCount(): Int = playerList.size
}
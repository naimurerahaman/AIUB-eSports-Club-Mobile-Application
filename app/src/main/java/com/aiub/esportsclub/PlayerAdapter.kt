package com.aiub.esportsclub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(
    private val context: Context,
    private val playerList: List<Player>
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvPlayerAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val tvGame: TextView = itemView.findViewById(R.id.tvPlayerGame)
        val tvRank: TextView = itemView.findViewById(R.id.tvPlayerRank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playerList[position]

        // Show the first letter of the player's name as the avatar
        holder.tvAvatar.text = player.name.first().toString()
        holder.tvName.text = player.name
        holder.tvGame.text = player.game
        holder.tvRank.text = player.rank
    }

    override fun getItemCount(): Int = playerList.size
}
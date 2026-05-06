package com.aiub.esportsclub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// A simple data class to hold one registration's data
data class Registration(
    val name: String,
    val studentId: String,
    val teamName: String
)

class RegistrationAdapter(
    private val context: Context,
    private val list: List<Registration>
) : RecyclerView.Adapter<RegistrationAdapter.RegViewHolder>() {

    class RegViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvRegName)
        val tvStudentId: TextView = itemView.findViewById(R.id.tvRegStudentId)
        val tvTeam: TextView = itemView.findViewById(R.id.tvRegTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_registration, parent, false)
        return RegViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegViewHolder, position: Int) {
        val reg = list[position]
        holder.tvName.text = "👤 ${reg.name}"
        holder.tvStudentId.text = "🎓 Student ID: ${reg.studentId}"
        holder.tvTeam.text = "⚔️ Team: ${reg.teamName}"
    }

    override fun getItemCount(): Int = list.size
}
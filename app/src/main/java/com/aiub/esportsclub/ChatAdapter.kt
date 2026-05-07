package com.aiub.esportsclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ChatAdapter manages the list of chat messages
// It decides whether to show a user bubble or a bot bubble
// for each message based on the isUser flag
class ChatAdapter(
    private val messageList: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // We define two "view types" — one for user, one for bot
    // RecyclerView uses these numbers to know which layout to inflate
    companion object {
        const val VIEW_TYPE_USER = 1  // User message = type 1
        const val VIEW_TYPE_BOT  = 2  // Bot message  = type 2
    }

    // ===== VIEW HOLDERS =====
    // One ViewHolder for user bubble
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
    }

    // One ViewHolder for bot bubble
    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
    }

    // ===== getItemViewType =====
    // This tells RecyclerView which type (user or bot) each item is
    // RecyclerView calls this before building each card
    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isUser) VIEW_TYPE_USER
        else VIEW_TYPE_BOT
    }

    // ===== onCreateViewHolder =====
    // Build the correct layout based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            // Build user bubble from item_chat_user.xml
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            // Build bot bubble from item_chat_bot.xml
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false)
            BotViewHolder(view)
        }
    }

    // ===== onBindViewHolder =====
    // Fill the bubble with the message text
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = messageList[position]

        if (holder is UserViewHolder) {
            holder.tvMessage.text = chatMessage.message
        } else if (holder is BotViewHolder) {
            holder.tvMessage.text = chatMessage.message
        }
    }

    override fun getItemCount(): Int = messageList.size

    // ===== Helper function to add a new message =====
    // Called every time user sends a message or bot replies
    fun addMessage(message: ChatMessage) {
        messageList.add(message)
        // Tell RecyclerView a new item was added at the bottom
        notifyItemInserted(messageList.size - 1)
    }
}
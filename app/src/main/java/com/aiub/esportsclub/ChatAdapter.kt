package com.aiub.esportsclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messageList: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER   = 1
        const val VIEW_TYPE_BOT    = 2
        const val VIEW_TYPE_TYPING = 3  // NEW — typing indicator type
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
    }

    // ViewHolder for typing indicator — no views to find, just shows the layout
    class TypingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return when {
            message.isTyping -> VIEW_TYPE_TYPING  // Show typing bubble
            message.isUser   -> VIEW_TYPE_USER    // Show user bubble
            else             -> VIEW_TYPE_BOT     // Show bot bubble
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER   -> UserViewHolder(
                inflater.inflate(R.layout.item_chat_user, parent, false))
            VIEW_TYPE_TYPING -> TypingViewHolder(
                inflater.inflate(R.layout.item_chat_typing, parent, false))
            else             -> BotViewHolder(
                inflater.inflate(R.layout.item_chat_bot, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = messageList[position]
        when (holder) {
            is UserViewHolder -> holder.tvMessage.text = chatMessage.message
            is BotViewHolder  -> holder.tvMessage.text = chatMessage.message
            // TypingViewHolder needs nothing — the layout shows automatically
        }
    }

    override fun getItemCount(): Int = messageList.size

    // Add a new message and scroll to it
    fun addMessage(message: ChatMessage) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    // ===== SHOW TYPING INDICATOR =====
    // Adds a "Typing..." bubble and returns its position
    // so we can remove it later
    fun showTyping(): Int {
        messageList.add(ChatMessage(isTyping = true))
        val position = messageList.size - 1
        notifyItemInserted(position)
        return position
    }

    // ===== REMOVE TYPING INDICATOR =====
    // Removes the typing bubble once the real reply arrives
    fun removeTyping(position: Int) {
        if (position >= 0 && position < messageList.size) {
            messageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
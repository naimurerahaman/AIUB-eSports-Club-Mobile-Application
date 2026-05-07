package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatbotFragment : Fragment() {

    // Our list of messages — starts empty
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter     : ChatAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose  = view.findViewById<Button>(R.id.btnCloseChatbot)
        val etInput   = view.findViewById<EditText>(R.id.etChatInput)
        val btnSend   = view.findViewById<Button>(R.id.btnSendMessage)
        recyclerView  = view.findViewById(R.id.recyclerViewChat)

        // ===== SET UP RECYCLERVIEW =====
        val layoutManager = LinearLayoutManager(requireContext())
        // stackFromEnd = true makes the list start from the bottom
        // so the latest message is always visible without scrolling
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter

        // ===== SHOW WELCOME MESSAGE FROM BOT =====
        addBotMessage("👋 Hi! I'm your AIUB eSports Club assistant!\n\nAsk me anything about:\n• Events & Tournaments\n• Registration\n• Players\n• Profile settings\n• Password help")

        // ===== CLOSE BUTTON =====
        btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== SEND BUTTON CLICK =====
        btnSend.setOnClickListener {
            sendMessage(etInput)
        }

        // ===== KEYBOARD SEND (user taps "Send" on keyboard) =====
        etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(etInput)
                true
            } else false
        }
    }

    // ===== SEND MESSAGE FUNCTION =====
    private fun sendMessage(etInput: EditText) {
        val userText = etInput.text.toString().trim()

        // Don't send empty messages
        if (userText.isEmpty()) return

        // 1. Add user's message to the chat (right side blue bubble)
        addUserMessage(userText)

        // 2. Clear the input field
        etInput.text.clear()

        // 3. Get bot's reply from the ChatbotEngine
        val botReply = ChatbotEngine.getReply(userText)

        // 4. Small delay before bot replies — feels more natural
        // postDelayed runs the code inside {} after 600 milliseconds
        recyclerView.postDelayed({
            // Only run if fragment is still attached
            if (isAdded) {
                addBotMessage(botReply)
            }
        }, 600)
    }

    // ===== ADD USER MESSAGE =====
    private fun addUserMessage(text: String) {
        // isUser = true → right side bubble
        adapter.addMessage(ChatMessage(text, isUser = true))
        scrollToBottom()
    }

    // ===== ADD BOT MESSAGE =====
    private fun addBotMessage(text: String) {
        // isUser = false → left side bubble
        adapter.addMessage(ChatMessage(text, isUser = false))
        scrollToBottom()
    }

    // ===== SCROLL TO BOTTOM =====
    // Always scroll to the latest message after adding one
    private fun scrollToBottom() {
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    // Hide FAB when chat opens
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setChatbotFabVisible(false)
    }

    // Show FAB again when chat closes
    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).setChatbotFabVisible(true)
    }
}
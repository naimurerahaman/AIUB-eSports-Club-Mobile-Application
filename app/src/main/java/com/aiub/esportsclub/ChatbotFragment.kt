package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ChatbotFragment : Fragment() {

    private val messageList    = mutableListOf<ChatMessage>()
    private lateinit var adapter      : ChatAdapter
    private lateinit var recyclerView : RecyclerView

    // Create one instance of GeminiChatService
    // "lazy" means it is only created the first time it is used
    // not immediately when the Fragment is created
    private val geminiService by lazy { GeminiChatService() }

    // Track if the bot is currently waiting for a response
    // We use this to prevent the user from sending multiple messages
    // while waiting for a reply
    private var isWaitingForReply = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<Button>(R.id.btnCloseChatbot)
        val etInput  = view.findViewById<EditText>(R.id.etChatInput)
        val btnSend  = view.findViewById<Button>(R.id.btnSendMessage)
        recyclerView = view.findViewById(R.id.recyclerViewChat)

        // ===== SET UP RECYCLERVIEW =====
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter

        // ===== WELCOME MESSAGE =====
        addBotMessage("👋 Hi! I'm your AI-powered AIUB eSports Club assistant!\n\nI can answer any question about the app, events, registration, and the club. What would you like to know?")

        // ===== CLOSE BUTTON =====
        btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== SEND BUTTON =====
        btnSend.setOnClickListener {
            sendMessage(etInput, btnSend)
        }

        // ===== KEYBOARD SEND =====
        etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(etInput, btnSend)
                true
            } else false
        }
    }

    // ===== MAIN SEND FUNCTION =====
    private fun sendMessage(etInput: EditText, btnSend: Button) {
        // TEMPORARY DEBUG — shows what the API key value actually is
        // We will remove this after fixing
//        android.util.Log.d("GEMINI_DEBUG", "API Key = '${BuildConfig.GEMINI_API_KEY}'")
//        android.widget.Toast.makeText(
//            requireContext(),
//            "Key starts with: ${BuildConfig.GEMINI_API_KEY.take(10)}",
//            android.widget.Toast.LENGTH_LONG
//        ).show()
        // Don't send if already waiting for a reply
        if (isWaitingForReply) {
            Toast.makeText(requireContext(), "Please wait for a response...", Toast.LENGTH_SHORT).show()
            return
        }

        val userText = etInput.text.toString().trim()
        if (userText.isEmpty()) return

        // 1. Show user message on screen
        addUserMessage(userText)

        // 2. Clear input field
        etInput.text.clear()

        // 3. Disable send button while waiting
        isWaitingForReply = true
        btnSend.isEnabled = false

        // 4. Show typing indicator and remember its position
        val typingPosition = adapter.showTyping()
        scrollToBottom()

        // ===== CALL GEMINI API =====
        // lifecycleScope.launch runs code in the background
        // "launch" = start a background task
        // The code inside {} runs without freezing the UI
        // This is called a "Coroutine"
        //
        // Think of it like ordering pizza:
        // You place the order (launch)
        // You do other things while waiting (UI stays responsive)
        // Pizza arrives (geminiService.sendMessage returns)
        // You eat it (show the response)
        lifecycleScope.launch {
            // geminiService.sendMessage() sends the text to Google's AI servers
            // "await" the response — the coroutine pauses here
            // but the UI thread stays responsive
            val botReply = geminiService.sendMessage(userText)

            // Back on the main thread — update the UI
            // Remove typing indicator
            adapter.removeTyping(typingPosition)

            // Show the actual reply
            if (isAdded) { // Make sure fragment is still attached
                addBotMessage(botReply)
                btnSend.isEnabled  = true
                isWaitingForReply  = false
            }
        }
    }

    private fun addUserMessage(text: String) {
        adapter.addMessage(ChatMessage(message = text, isUser = true))
        scrollToBottom()
    }

    private fun addBotMessage(text: String) {
        adapter.addMessage(ChatMessage(message = text, isUser = false))
        scrollToBottom()
    }

    private fun scrollToBottom() {
        if (adapter.itemCount > 0) {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }

    // Hide FAB when chat is open
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setChatbotFabVisible(false)
    }

    // Show FAB again when chat is closed
    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).setChatbotFabVisible(true)
    }
}
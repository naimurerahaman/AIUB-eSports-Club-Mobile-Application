package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

    private val messageList           = mutableListOf<ChatMessage>()
    private lateinit var adapter      : ChatAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private val geminiService    by lazy { GeminiChatService() }
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
        layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter

        // ===== AUTO SCROLL WHEN KEYBOARD OPENS =====
        recyclerView.addOnLayoutChangeListener {
                _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && adapter.itemCount > 0) {
                recyclerView.postDelayed({
                    recyclerView.smoothScrollToPosition(
                        adapter.itemCount - 1
                    )
                }, 100)
            }
        }

        // ===== WELCOME MESSAGE =====
        addBotMessage(
            "👋 Hi! I'm your AI-powered AIUB eSports Club assistant!\n\n" +
                    "Ask me anything about the app, events, registration, and the club."
        )

        // ===== BUTTONS =====
        btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSend.setOnClickListener {
            sendMessage(etInput, btnSend)
        }

        etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(etInput, btnSend)
                true
            } else false
        }
    }

    private fun sendMessage(etInput: EditText, btnSend: Button) {
        if (isWaitingForReply) {
            Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_SHORT).show()
            return
        }

        val userText = etInput.text.toString().trim()
        if (userText.isEmpty()) return

        addUserMessage(userText)
        etInput.text.clear()

        isWaitingForReply = true
        btnSend.isEnabled = false

        val typingPosition = adapter.showTyping()
        scrollToBottom()

        lifecycleScope.launch {
            val botReply = geminiService.sendMessage(userText)
            adapter.removeTyping(typingPosition)

            if (isAdded) {
                addBotMessage(botReply)
                btnSend.isEnabled = true
                isWaitingForReply = false
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
            recyclerView.post {
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    // ===== APPLY adjustResize WHEN CHAT OPENS =====
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setChatbotFabVisible(false)

        // Force adjustResize for this fragment specifically
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    // ===== RESTORE WHEN CHAT CLOSES =====
    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).setChatbotFabVisible(true)

        // Restore normal mode for other fragments
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }
}
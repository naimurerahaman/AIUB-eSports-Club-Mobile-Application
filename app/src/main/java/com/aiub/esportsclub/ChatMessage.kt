package com.aiub.esportsclub

// Added a new "isTyping" flag
// When isTyping = true, we show the "..." typing bubble
// while waiting for Gemini to respond
data class ChatMessage(
    val message  : String  = "",
    val isUser   : Boolean = false,
    val isTyping : Boolean = false  // NEW — shows typing indicator
)
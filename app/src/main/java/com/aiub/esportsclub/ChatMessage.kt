package com.aiub.esportsclub

// This holds ONE message in the chat
// Every bubble in the chat — whether from user or bot — is one ChatMessage object
data class ChatMessage(
    val message  : String,  // The actual text of the message
    val isUser   : Boolean  // true = message from user, false = message from bot
    // We use isUser to decide which side to show the bubble on
    // true  → right side (blue bubble) = user sent this
    // false → left side (gray bubble)  = bot sent this
)
package com.aiub.esportsclub

// A data class is a simple class that only holds data
// It automatically creates useful functions like toString() and copy()
// Think of this like a template for one event's information
data class Event(
    val icon: String,       // Emoji for the game (e.g. "🔫")
    val name: String,       // Name of the tournament
    val date: String,       // Date of the event
    val prize: String,      // Prize money or reward
    val description: String // Full description for detail screen
)
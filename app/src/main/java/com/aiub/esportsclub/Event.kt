package com.aiub.esportsclub

// Updated Event data class with all new fields
data class Event(
    val documentId      : String = "", // Firebase document ID
    val icon            : String = "🎮",
    val name            : String = "",
    val date            : String = "",
    val prize           : String = "",
    val description     : String = "",
    val registrationLink: String = "", // NEW — Google Form or signup link
    val eventLink       : String = "", // NEW — Facebook/Discord/tournament link
    val bannerImageUrl  : String = ""  // NEW — uploaded banner image URL
)
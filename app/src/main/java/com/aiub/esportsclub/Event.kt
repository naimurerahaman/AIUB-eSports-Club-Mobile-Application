package com.aiub.esportsclub

data class Event(
    val documentId      : String = "",
    val icon            : String = "🎮",
    val name            : String = "",
    val game            : String = "", // ← this was missing
    val date            : String = "",
    val prize           : String = "",
    val description     : String = "",
    val registrationLink: String = "",
    val eventLink       : String = "",
    val bannerImageUrl  : String = ""
)
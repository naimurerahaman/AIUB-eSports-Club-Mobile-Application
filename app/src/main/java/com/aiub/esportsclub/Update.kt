package com.aiub.esportsclub

// Added "link" field for optional URL
data class Update(
    val documentId  : String = "",
    val title       : String = "",
    val description : String = "",
    val imageUrl    : String = "",
    val link        : String = "", // NEW — optional clickable URL
    val timestamp   : Long   = 0L
)
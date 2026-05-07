package com.aiub.esportsclub

// This data class holds the information for ONE update/post
// Think of it like a template for a single Facebook post
data class Update(
    val documentId  : String = "", // Firebase unique ID for this post
    val title       : String = "", // Headline of the post
    val description : String = "", // Full text of the post
    val imageUrl    : String = "", // Optional image URL (can be empty)
    val timestamp   : Long   = 0L  // When it was posted — used for sorting
)
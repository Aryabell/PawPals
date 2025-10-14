package com.example.pawpals.community

data class Reply(
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String,
    val imageUri: String? = null
)

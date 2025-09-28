package com.example.pawpals

data class Reply(
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String,
    val imageUri: String? = null
)

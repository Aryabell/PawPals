package com.example.pawpals.message

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

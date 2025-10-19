package com.example.pawpals.message

data class ChatPreview(
    val chatId: String = "",
    val userName: String = "",
    val lastMessage: String = "",
    val userImageResId: Int = 0,
    val timestamp: Long = 0L
)

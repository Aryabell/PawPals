package com.example.pawpals.message

data class ChatPreview(
    val chat_id: Int,
    val user_id: Int,
    val user_name: String,
    val last_message: String?,
    val timestamp: Long?
)

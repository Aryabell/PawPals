package com.example.pawpals.message

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("sender_id")
    val senderId: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("created_at")
    val timestamp: String
)

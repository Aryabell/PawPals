package com.example.pawpals.notification

data class NotificationModel(
    val id: Int,
    val message: String,
    val type: String,
    val created_at: String
)

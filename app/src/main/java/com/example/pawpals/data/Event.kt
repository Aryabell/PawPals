package com.example.pawpals.data

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val isJoined: Boolean = false,
    val imageUrl: String? = null
)

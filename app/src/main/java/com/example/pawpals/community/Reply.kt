package com.example.pawpals.community

data class Reply(
    val id: String,
    val post_id: String,
    val author: String,
    val content: String,
    val timestamp: Long,
    val image_path: String? = null
)

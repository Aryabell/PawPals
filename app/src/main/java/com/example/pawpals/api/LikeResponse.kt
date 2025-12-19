package com.example.pawpals.api

data class LikeResponse(
    val success: Boolean,
    val isLiked: Boolean,
    val like_count: Int
)

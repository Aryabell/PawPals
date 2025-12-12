package com.example.pawpals.model

import java.io.Serializable

data class Post(
    val id: String,
    val content: String,
    val author: String,
    val timestamp: Long,
    val category: String,
    val imageUri: String? = null,
    val userRole: String,
    val commentCount: Int,
    var likeCount: Int,
    val userAvatar: Int,
    var isLiked: Boolean = false,
    var isTrending: Boolean = false,
    var isHidden: Boolean = false
) : Serializable
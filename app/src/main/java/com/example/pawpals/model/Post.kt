package com.example.pawpals.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: String,
    val content: String,
    val author: String,
    val category: String,
    @SerializedName("image_uri")
    val imageUri: String?,
    @SerializedName("user_role")
    val userRole: String,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("like_count")
    var likeCount: Int,
    @SerializedName("is_trending")
    var isTrending: Boolean,
    @SerializedName("is_hidden")
    var isHidden: Boolean,
    val created_at: String,
    var isLiked: Boolean = false
)

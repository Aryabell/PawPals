package com.example.pawpals.model

import com.google.gson.annotations.SerializedName

data class Reply(
    val id: String,
    val post_id: String,
    val author: String,
    val content: String,
    @SerializedName("image_uri")
    val imageUri: String?,
    @SerializedName("created_at")
    val createdAt: String
)


package com.example.pawpals

import java.io.Serializable

data class Post(
    val id: String,
    val content: String,
    val author: String,
    val timestamp: String,
    val category: String,
    val imageUri: String? = null
) : Serializable

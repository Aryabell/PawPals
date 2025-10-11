package com.example.pawpals

import java.io.Serializable

data class Post(
    val id: String,              // ID post
    val content: String,         // Konten post
    val author: String,          // Nama pengguna
    val timestamp: String,       // Waktu
    val category: String,        // Tag komunitas
    val imageUri: String? = null, // Bisa null
    val userRole: String,        // Role pengguna
    val commentCount: Int,       // Jumlah komentar
    var likeCount: Int,          // Jumlah like
    val userAvatar: Int,         // Avatar drawable
    var isLiked: Boolean = false // like post
) : Serializable

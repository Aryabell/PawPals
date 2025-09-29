package com.example.pawpals

import java.io.Serializable

data class Post(
    // Properti inti dari struktur lama yang dimodifikasi
    val id: String, // Dari String
    val content: String, // Sama dengan contentText
    val author: String, // Sama dengan username
    val timestamp: String, // Sama dengan timeAgo
    val category: String, // Sama dengan communityTag
    val imageUri: String? = null, // Sama dengan imageUri (biar compatible dengan real data)

    // Properti Tambahan untuk UI (dari dummy data sebelumnya)
    val userRole: String, // Role: "Pengurus", "Anggota", dll.
    val commentCount: Int,
    val likeCount: Int,
    val userAvatar: Int // Resource ID dari drawable avatar (Int) - untuk tampilan sementara
) : Serializable

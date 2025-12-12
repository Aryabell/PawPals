package com.example.pawpals.model

data class Member(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val blocked: Int
)

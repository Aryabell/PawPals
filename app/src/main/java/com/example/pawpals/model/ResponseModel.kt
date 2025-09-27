package com.example.pawpals.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profile_pic: String,
    val role: String?
)

data class ResponseModel(
    val success: Boolean,
    val message: String,
    val user: User?
)

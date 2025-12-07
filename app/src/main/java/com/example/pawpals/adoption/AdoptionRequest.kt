package com.example.pawpals.adoption


data class AdoptionRequest(
    val id: Int,
    val userId: String,
    val dogId: Int,
    val dogName: String,
    val adopterName: String,
    val adopterAddress: String,
    val adopterPhone: String,
    val reason: String,
    val status: String,
    val createdAt: String
)

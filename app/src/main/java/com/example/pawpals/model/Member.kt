package com.example.pawpals.model

data class Member(
    var name: String,
    var email: String,
    var password: String,
    var role: String,
    var blocked: Boolean
)
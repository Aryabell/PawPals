package com.example.pawpals.model

import com.google.firebase.firestore.Exclude

data class Member(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var role: String = "Member",
    var blocked: Boolean = false,
    @get:Exclude var id: String = ""
)

package com.example.pawpals.data

import com.google.gson.annotations.SerializedName

data class Event(
    val id: Int,
    val title: String,
    val description: String,

    @SerializedName("event_date")
    val date: String,

    val location: String,

    @SerializedName("isJoined")
    val isJoined: Boolean,

    @SerializedName("image_url")
    val imageUrl: String?
)

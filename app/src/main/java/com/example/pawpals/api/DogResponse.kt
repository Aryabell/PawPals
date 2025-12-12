package com.example.pawpals.api

import com.example.pawpals.model.Dog

data class DogResponse(
    val status: String,
    val message: String,
    val dogs: List<Dog>
)


package com.example.pawpals.model

data class ScanResponse(
    val success: Boolean,
    val penyakit: String,
    val akurasi: String,
    val pesan: String
)
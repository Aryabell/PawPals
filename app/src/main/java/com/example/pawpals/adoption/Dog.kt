package com.example.pawpals.adoption

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val id: Int,
    val name: String,
    val breed: String,
    val gender: Gender,
    val location: String,
    val imageUrl: String,
    val ageInYears: Int,
    val weightKg: Double,
    val ownerName: String,
    val ownerPhone: String,
    val ownerMessageHandle: String // misal username atau bisa kosong
) : Parcelable

enum class Gender { MALE, FEMALE }

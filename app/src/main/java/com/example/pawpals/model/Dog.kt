package com.example.pawpals.model

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
    val ownerId: Int,
    val ownerName: String?,
    val ownerPhone: String?,
    val ownerMessageHandle: String?
) : Parcelable


@Parcelize
enum class Gender : Parcelable {
    MALE, FEMALE
}
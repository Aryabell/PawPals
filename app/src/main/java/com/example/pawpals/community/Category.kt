package com.example.pawpals.community

import androidx.annotation.DrawableRes

data class Category(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val iconResId: Int
)

package com.example.fitpal.model

data class Gym(
    val id: String,
    val name: String,
    val rating: Float?,
    val location: Pair<Float, Float>,
    val address: String,
    val reviews: List<Review>
)

package com.example.fitpal.model.fitness

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: String // Can be "10" or "30 seconds" or similar
)
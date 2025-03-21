package com.example.fitpal.model.fitness


import java.util.Date

interface FitnessContent {
    val id: String
    val title: String
    val content: String
    val createdAt: Date
}
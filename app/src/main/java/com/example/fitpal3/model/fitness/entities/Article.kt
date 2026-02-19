package com.example.fitpal3.model.fitness.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitpal3.model.fitness.FitnessContent
import java.util.Date

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey override val id: String,
    override val title: String,
    override val content: String,
    val imageUrl: String? = null,
    val category: String,
    override val createdAt: Date = Date()
) : FitnessContent
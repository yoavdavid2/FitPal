package com.example.fitpal3.model.fitness.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitpal3.model.fitness.FitnessContent
import java.util.Date

@Entity(tableName = "tips")
data class Tip(
    @PrimaryKey override val id: String,
    override val title: String,
    override val content: String,
    override val createdAt: Date = Date()
) : FitnessContent
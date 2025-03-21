package com.example.fitpal.model.fitness.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitpal.model.fitness.Exercise
import com.example.fitpal.model.fitness.FitnessContent
import java.util.Date
import java.util.UUID

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey override val id: String,
    override val title: String,
    override val content: String,
    val difficulty: String,
    val duration: String,
    val targetMuscleGroup: String,
    override val createdAt: Date = Date()
) : FitnessContent
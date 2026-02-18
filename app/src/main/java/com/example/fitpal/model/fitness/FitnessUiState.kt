package com.example.fitpal.model.fitness

import com.example.fitpal.model.fitness.entities.Article
import com.example.fitpal.model.fitness.entities.Tip
import com.example.fitpal.model.fitness.entities.WorkoutPlan

data class FitnessUiState(
    val tips: List<Tip> = emptyList(),
    val articles: List<Article> = emptyList(),
    val workoutPlans: List<WorkoutPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

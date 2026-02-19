package com.example.fitpal3.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemWorkoutPlanBinding
import com.example.fitpal3.model.fitness.entities.WorkoutPlan

class WorkoutPlanViewHolder(
    private val binding: ItemWorkoutPlanBinding,
    private val onWorkoutPlanClicked: (WorkoutPlan) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(workoutPlan: WorkoutPlan) {
        binding.workoutTitle.text = workoutPlan.title
        binding.workoutDifficulty.text = "Difficulty: ${workoutPlan.difficulty}"
        binding.workoutDuration.text = "Duration: ${workoutPlan.duration}"
        binding.workoutTarget.text = "Target: ${workoutPlan.targetMuscleGroup}"

        binding.root.setOnClickListener {
            onWorkoutPlanClicked(workoutPlan)
        }
    }
}
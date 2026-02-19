package com.example.fitpal3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemWorkoutPlanBinding
import com.example.fitpal3.model.fitness.entities.WorkoutPlan

class WorkoutPlanAdapter(private val onWorkoutPlanClicked: (WorkoutPlan) -> Unit) :
    RecyclerView.Adapter<WorkoutPlanViewHolder>() {

    private val workoutPlans = mutableListOf<WorkoutPlan>()

    fun updateWorkoutPlans(newWorkoutPlans: List<WorkoutPlan>) {
        workoutPlans.clear()
        workoutPlans.addAll(newWorkoutPlans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutPlanViewHolder {
        val binding = ItemWorkoutPlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkoutPlanViewHolder(binding, onWorkoutPlanClicked)
    }

    override fun onBindViewHolder(holder: WorkoutPlanViewHolder, position: Int) {
        holder.bind(workoutPlans[position])
    }

    override fun getItemCount(): Int = workoutPlans.size
}
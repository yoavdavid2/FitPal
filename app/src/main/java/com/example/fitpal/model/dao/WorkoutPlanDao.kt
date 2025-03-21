package com.example.fitpal.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal.model.fitness.entities.WorkoutPlan

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans ORDER BY createdAt DESC")
    fun getAllWorkoutPlans(): List<WorkoutPlan>

    @Query("SELECT * FROM workout_plans WHERE id = :workoutPlanId")
    fun getWorkoutPlanById(workoutPlanId: String): WorkoutPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkoutPlans(workoutPlans: List<WorkoutPlan>)

    @Query("DELETE FROM workout_plans")
    fun deleteAllWorkoutPlans()
}
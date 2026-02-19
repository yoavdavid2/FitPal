package com.example.fitpal3.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal3.model.dao.AppLocalDB
import com.example.fitpal3.model.fitness.entities.WorkoutPlan
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WorkoutDetailsViewModel : ViewModel() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val database = AppLocalDB.database

    private val _workoutPlan = MutableLiveData<WorkoutPlan?>()
    val workoutPlan: LiveData<WorkoutPlan?> = _workoutPlan

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadWorkoutPlan(workoutPlanId: String) {
        _loading.value = true

        executor.execute {
            try {
                val workoutPlanFromDb = database.workoutPlanDao().getWorkoutPlanById(workoutPlanId)

                _workoutPlan.postValue(workoutPlanFromDb)
                _loading.postValue(false)

            } catch (e: Exception) {
                _error.postValue("Failed to load workout plan: ${e.message}")
                _loading.postValue(false)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
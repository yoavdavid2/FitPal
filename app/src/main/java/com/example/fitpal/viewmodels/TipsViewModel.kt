package com.example.fitpal.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitpal.model.dao.AppLocalDB
import com.example.fitpal.model.fitness.entities.Article
import com.example.fitpal.model.fitness.entities.Tip
import com.example.fitpal.model.fitness.entities.WorkoutPlan
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TipsViewModel(application: Application) : AndroidViewModel(application) {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val database = AppLocalDB.database
    private val sharedPreferences =
        application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _tips = MutableLiveData<List<Tip>>()
    val tips: LiveData<List<Tip>> = _tips

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _workoutPlans = MutableLiveData<List<WorkoutPlan>>()
    val workoutPlans: LiveData<List<WorkoutPlan>> = _workoutPlans


    init {
        _tips.value = emptyList()
        _articles.value = emptyList()
        _workoutPlans.value = emptyList()
    }

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }

    fun loadSavedContent() {
        setLoading(true)

        executor.execute {
            try {
                val loadedTips = database.tipDao().getAllTips()
                val loadedArticles = database.articleDao().getAllArticles()
                val loadedWorkoutPlans = database.workoutPlanDao().getAllWorkoutPlans()

                _tips.postValue(loadedTips)
                _articles.postValue(loadedArticles)
                _workoutPlans.postValue(loadedWorkoutPlans)

                _error.postValue(null)
                _loading.postValue(false)
            } catch (e: Exception) {
                _error.postValue("Failed to load saved content: ${e.message}")
                _loading.postValue(false)
            }
        }
    }

    fun saveContent(tips: List<Tip>, articles: List<Article>, workoutPlans: List<WorkoutPlan>) {
        executor.execute {
            try {
                _tips.postValue(tips)
                _articles.postValue(articles)
                _workoutPlans.postValue(workoutPlans)

                with(database) {
                    tipDao().deleteAllTips()
                    articleDao().deleteAllArticles()
                    workoutPlanDao().deleteAllWorkoutPlans()

                    tipDao().insertTips(tips)
                    articleDao().insertArticles(articles)
                    workoutPlanDao().insertWorkoutPlans(workoutPlans)
                }

                _error.postValue(null)
                _loading.postValue(false)
            } catch (e: Exception) {
                _error.postValue("Failed to save content: ${e.message}")
                _loading.postValue(false)
            }
        }
    }

    fun canGenerateContent(): Boolean {
        val lastGeneratedTime = sharedPreferences.getLong("last_content_generation_time", 0)
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000L

        return (currentTime - lastGeneratedTime) < twentyFourHoursInMillis
    }
}
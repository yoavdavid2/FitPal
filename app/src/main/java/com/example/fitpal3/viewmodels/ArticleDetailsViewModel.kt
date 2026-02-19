package com.example.fitpal3.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal3.model.dao.AppLocalDB
import com.example.fitpal3.model.fitness.entities.Article
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ArticleDetailsViewModel : ViewModel() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val database = AppLocalDB.database

    private val _article = MutableLiveData<Article?>()
    val article: LiveData<Article?> = _article

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadArticle(articleId: String) {
        _loading.postValue(true)

        executor.execute {
            try {
                val articleFromDb = database.articleDao().getArticleById(articleId)

                _article.postValue(articleFromDb)
                _loading.postValue(false)

            } catch (e: Exception) {
                _error.postValue("Failed to load article: ${e.message}")
                _loading.postValue(false)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
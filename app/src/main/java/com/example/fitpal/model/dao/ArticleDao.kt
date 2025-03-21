package com.example.fitpal.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal.model.fitness.entities.Article

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY createdAt DESC")
    fun getAllArticles(): List<Article>

    @Query("SELECT * FROM articles WHERE id = :articleId")
    fun getArticleById(articleId: String): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<Article>)

    @Query("DELETE FROM articles")
    fun deleteAllArticles()
}
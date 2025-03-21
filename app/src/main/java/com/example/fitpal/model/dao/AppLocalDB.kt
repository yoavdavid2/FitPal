package com.example.fitpal.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitpal.base.MyApplication
import com.example.fitpal.model.Chat
import com.example.fitpal.model.Post
import com.example.fitpal.model.fitness.entities.Article
import com.example.fitpal.model.fitness.entities.Tip
import com.example.fitpal.model.fitness.entities.WorkoutPlan
import com.example.fitpal.utils.Converters

@TypeConverters(Converters::class)
@Database(entities = [Tip::class, Article::class, WorkoutPlan::class, Post::class, Chat::class], version = 4)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun tipDao(): TipDao
    abstract fun articleDao(): ArticleDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun postDao(): PostDao
    abstract fun chatDao(): ChatDao
}

object AppLocalDB {
    val database: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.context
            ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
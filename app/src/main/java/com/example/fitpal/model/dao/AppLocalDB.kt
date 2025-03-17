package com.example.fitpal.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitpal.base.MyApplication
import com.example.fitpal.model.Post

@Database(entities = [Post::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun PostDao(): PostDao
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
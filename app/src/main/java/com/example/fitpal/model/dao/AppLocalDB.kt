package com.example.fitpal.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitpal.base.MyApplication
import com.example.fitpal.model.Post
import com.example.fitpal.utils.extensions.Converters

@TypeConverters(Converters::class)  // Convert nested types to json and the opposite
@Database(entities = [Post::class], version = 2)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
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
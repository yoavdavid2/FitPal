package com.example.fitpal.model.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal.model.Post

@Dao
interface PostDao  {

    @Query("SELECT * FROM Posts")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM Posts WHERE id =:id")
    fun getPostById(id: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

    @Delete
    fun delete(post: Post)
}
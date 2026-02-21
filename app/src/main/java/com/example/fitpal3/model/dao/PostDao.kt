package com.example.fitpal3.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal3.model.Post

@Dao
interface PostDao  {

    @Query("DELETE FROM Post")
    fun clearPostsTable()

    @Query("SELECT * FROM Post ORDER BY lastUpdated DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM Post WHERE author = :author ORDER BY lastUpdated DESC")
    fun getPostsByAuthor(author: String): LiveData<List<Post>>
}
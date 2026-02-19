package com.example.fitpal3.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal3.Comment

@Dao
interface CommentsDao  {

    @Query("DELETE FROM Comment")
    fun clearComnmentsTable()

    @Query("SELECT * FROM Comment")
    fun getAllComments(): LiveData<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg comment: Comment)

    @Delete
    fun delete(comment: Comment)
}
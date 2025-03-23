package com.example.fitpal.model.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal.Comment

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
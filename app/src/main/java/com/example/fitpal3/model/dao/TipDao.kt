package com.example.fitpal3.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal3.model.fitness.entities.Tip

@Dao
interface TipDao {
    @Query("SELECT * FROM tips ORDER BY createdAt DESC")
    fun getAllTips(): List<Tip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTips(tips: List<Tip>)

    @Query("DELETE FROM tips")
    fun deleteAllTips()
}
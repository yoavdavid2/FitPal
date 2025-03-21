package com.example.fitpal.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitpal.model.Chat
import com.example.fitpal.model.fitness.entities.Article

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastUpdated DESC")
    fun getAllChats(): LiveData<List<Chat>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChatById(chatId: String): Chat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChats(chats: List<Chat>)

    @Query("DELETE FROM chats")
    fun deleteAllChats()
}


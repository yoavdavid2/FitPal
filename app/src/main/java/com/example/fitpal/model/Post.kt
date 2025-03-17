package com.example.fitpal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val author: String,
    val title: String,
    val text: String,
    val image: Int,
    var likes: Int = 0,
    var comments: Int = 0,
    val date: String  ,
)

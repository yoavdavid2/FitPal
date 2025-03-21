package com.example.fitpal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comment(
    @PrimaryKey val id: String,
    val author: String,
    val text: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Comment {
            return Comment(
                id = map["id"] as? String ?: "",
                author = map["author"] as? String ?: "",
                text = map["text"] as? String ?: ""
            )
        }
    }
}
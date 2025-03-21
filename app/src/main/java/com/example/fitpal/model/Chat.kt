package com.example.fitpal.model

import com.google.firebase.Timestamp

data class Chat(
    val chatUsers: List<String>,
    val messages: List<Message>,
    val lastUpdated: Long? = null
)

data class Message(
    val messageText: String,
    val senderId: String,
    val timestamp: Timestamp
)
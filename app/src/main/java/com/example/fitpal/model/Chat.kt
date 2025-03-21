package com.example.fitpal.model

import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitpal.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey val id: String,
    val chatUsers: List<String>,
    val messages: List<Message>,
    val lastUpdated: Long? = null
) {

    companion object {

        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("CHAT_TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("CHAT_TAG", Context.MODE_PRIVATE)?.apply {
                        edit()
                            .putLong(LOCAL_LAST_UPDATED, value)
                            .apply()
                    }
            }

        const val ID_KEY = "id"
        const val CHAT_USERS_KEY = "chatUsers"
        const val MESSAGES_KEY = "messages"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "localChatLastUpdated"

        fun fromJSON(json: Map<String, Any>): Chat {
            val id = json[ID_KEY] as? String ?: ""
            val chatUsers = json[CHAT_USERS_KEY] as? List<String> ?: emptyList()
            val messages = convertHashMapListToMessages(json[MESSAGES_KEY] as? List<HashMap<String, Any>>)
            val timeStamp = json[LAST_UPDATED] as? Timestamp
            val lastUpdatedLongTimestamp = timeStamp?.toDate()?.time

            return Chat(
                id = id,
                chatUsers = chatUsers,
                messages = messages,
                lastUpdated = lastUpdatedLongTimestamp
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to id,
            CHAT_USERS_KEY to chatUsers,
            MESSAGES_KEY to messages.map { message ->
                message.json // Use message.json here
            },
            LAST_UPDATED to FieldValue.serverTimestamp(),
            LOCAL_LAST_UPDATED to FieldValue.serverTimestamp()
        )
}

@Entity
data class Message(
    @PrimaryKey val id: String,
    val messageText: String,
    val senderId: String,
    val timestamp: Timestamp
) {
    val json: Map<String, Any>
        get() = hashMapOf(
            "id" to id,
            "messageText" to messageText,
            "senderId" to senderId,
            "timestamp" to timestamp
        )
}

fun convertHashMapListToMessages(hashMaps: List<HashMap<String, Any>>?): List<Message> {
    if (hashMaps != null) {
        return hashMaps.mapNotNull { map ->
            try {
                Message(
                    id = map["id"] as String,
                    messageText = map["messageText"] as String,
                    senderId = map["senderId"] as String,
                    timestamp = map["timestamp"] as Timestamp // Ensure correct type
                )
            } catch (e: Exception) {
                Log.e("Firestore", "Error converting HashMap to Message: ${e.message}")
                null
            }
        }
    } else {
        return emptyList()
    }
}
package com.example.fitpal.model


import android.util.Log
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage



class FirebaseModel {

    private val database = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
    }

    fun getUserInboxMessages(activeUserEmail: String, callback: (List<Chat>) -> Unit) {
        database.collection("chats").whereArrayContains("chatUsers", activeUserEmail).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val currentUserChats: MutableList<Chat> = mutableListOf()
                    callback(currentUserChats)
                }

                false -> callback(listOf())
            }
        }
    }

    fun addChat(newChat: Chat ,callback: () -> Unit) {
        database.collection("chats").document().set(newChat).addOnCompleteListener { callback() }
            .addOnFailureListener{ Log.d(TAG,it.toString() + it.message) }

    }


    companion object {
        private const val TAG = "firebaseModel"
    }
}
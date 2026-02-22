package com.example.fitpal3.model

import android.graphics.Bitmap
import android.util.Log
import com.example.fitpal3.Comment
import com.example.fitpal3.base.Constants
import com.example.fitpal3.base.EmptyCallback
import com.example.fitpal3.base.PostsCallback
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import java.io.ByteArrayOutputStream
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.google.firebase.firestore.ListenerRegistration


class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = settings
    }

    fun getAllPosts(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection(Constants.Collections.POSTS)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            posts.add(Post.fromJSON(json.data))
                        }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun add(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS)
            .add(post.json)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                callback()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    fun like(postId: String, author: String, callback: EmptyCallback) {
        val postsCollection = database.collection(Constants.Collections.POSTS)

        postsCollection.whereEqualTo("id", postId).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.w("TAG", "Post not found with id: $postId")
                    return@addOnSuccessListener
                }

                val postRef = querySnapshot.documents.first().reference // Get the DocumentReference

                database.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)

                    val likes = snapshot.get("likes") as? List<String> ?: emptyList()

                    val updatedLikes = if (likes.contains(author)) {
                        likes - author
                    } else {
                        likes + author
                    }

                    transaction.update(postRef, "likes", updatedLikes)
                }.addOnSuccessListener {
                    Log.d("TAG", "Like list updated successfully")
                    callback()
                }.addOnFailureListener { e ->
                    Log.w("TAG", "Error updating likes", e)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error fetching post", e)
            }
    }

    fun addComment(postId: String, newComment: Comment, callback: (Boolean) -> Unit) {
        val postsCollection = database.collection(Constants.Collections.POSTS)

        postsCollection.whereEqualTo("id", postId).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.w("TAG", "Post not found with id: $postId")
                    return@addOnSuccessListener
                }

                val postRef = querySnapshot.documents.first().reference

                database.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)

                    val comments = snapshot.get("comments") as? List<String> ?: emptyList()
                    val updatedComments = comments + newComment

                    transaction.update(postRef, "comments", updatedComments)
                }.addOnSuccessListener {
                    return@addOnSuccessListener
                    Log.d("TAG", "Comment list updated successfully")
                }.addOnFailureListener { e ->
                    return@addOnFailureListener
                    Log.w("TAG", "Error updating comment", e)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error fetching post", e)
            }
    }

    fun getPostById(postId: String, callback: (Post?) -> Unit) {
        Log.d("TAG", "getPostById: $postId")
        database.collection(Constants.Collections.POSTS)
            .whereEqualTo("id", postId)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        Log.d("TAG_res_getPostById", it.result.toString())
                        if (it.result.isEmpty) {
                            callback(null)
                            return@addOnCompleteListener
                        }
                        val post: Post = Post.fromJSON(it.result.first().data)
                        callback(post)
                    }

                    false -> callback(null)
                }
            }
    }

    fun getUserInboxMessages(activeUserEmail: String, callback: (MutableList<Chat>) -> Unit) {
        database.collection("chats").whereArrayContains("chatUsers", activeUserEmail).get()
            .addOnSuccessListener { querySnapshot ->
                val currentUserChats: MutableList<Chat> = mutableListOf()

                for (document in querySnapshot) {
                    val documentData = document.data

                    val chatUsers = documentData["chatUsers"] as? List<String>

                    val messages = documentData["messages"] as? List<HashMap<String, Any>>
                    val parsedMessaged = convertHashMapListToMessages(messages)

                    val lastUpdated = documentData["lastUpdated"] as? Long
                    val id = document.id

                    chatUsers?.let {
                        if (messages != null) {
                            val chat = Chat(
                                chatUsers = it,
                                messages = parsedMessaged,
                                lastUpdated = lastUpdated,
                                id = id
                            )

                            currentUserChats.add(chat)
                        }
                    }

                }

                callback(currentUserChats)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting user chats", exception)
            }
    }

    fun addChat(newChat: Chat ,callback: (Boolean) -> Unit) {
        database.collection("chats").document().set(newChat).addOnCompleteListener { callback(true) }
            .addOnFailureListener{ Log.d(TAG,it.toString() + it.message)
                callback(false)}

    }

    private var chatListener: ListenerRegistration? = null

    fun listenToChatMessages(chatId: String, callback: (MutableList<Message>) -> Unit) {
        val chatDocRef = database.collection("chats").document(chatId)

        chatListener?.remove()

        chatListener = chatDocRef.addSnapshotListener { documentSnapshot, error ->
            if (error != null) {
                Log.d(TAG, "listenToChatMessages error", error)
                callback(mutableListOf())
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val messages = documentSnapshot["messages"] as? List<HashMap<String, Any>>
                val parsedMessages = convertHashMapListToMessages(messages)
                callback(parsedMessages.toMutableList())
            } else {
                callback(mutableListOf())
            }
        }
    }

    fun stopListeningToChatMessages() {
        chatListener?.remove()
        chatListener = null
    }


    fun addNewMessage(newMessage: Message, chatId: String ,callback: (Boolean) -> Unit) {
        val messageMap = mapOf(
            "id" to newMessage.id,
            "messageText" to newMessage.messageText,
            "senderId" to newMessage.senderId,
            "timestamp" to newMessage.timestamp
        )

        database.collection("chats").document(chatId)
            .update("messages", FieldValue.arrayUnion(messageMap))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding message", exception)
                callback(false)
            }
    }
    companion object {
        private const val TAG = "firebaseModel"
    }
    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            callback(null)
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }
    }
}
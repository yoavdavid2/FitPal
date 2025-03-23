package com.example.fitpal.model

import android.graphics.Bitmap
import android.util.Log
import com.example.fitpal.Comment
import com.example.fitpal.base.Constants
import com.example.fitpal.base.EmptyCallback
import com.example.fitpal.base.PostsCallback
import com.example.fitpal.utils.extensions.toFirebaseTimestamp
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


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
//            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED, sinceLastUpdated.toFirebaseTimestamp) //TDOD
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            Log.d("TAG_POSTS_JSON", json.toString())
                            posts.add(Post.fromJSON(json.data))
                        }
                        Log.d("TAG", "number of posts: " + posts.size.toString())
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

        // Query to find the document where "id" field matches postId
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
                        likes - author // Remove author if they already liked
                    } else {
                        likes + author // Add author if they haven't liked
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

        // Query to find the document where "id" field matches postId
        postsCollection.whereEqualTo("id", postId).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.w("TAG", "Post not found with id: $postId")
                    return@addOnSuccessListener
                }

                val postRef = querySnapshot.documents.first().reference // Get the DocumentReference

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

    fun delete(student: Post, callback: EmptyCallback) {

    }

//    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
//        val storageRef = storage.reference
//        val imageRef = storageRef.child("images/$name.jpg")
//        val baos = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        var uploadTask = imageRef.putBytes(data)
//        uploadTask.addOnFailureListener {
//            callback(null)
//        }.addOnSuccessListener { taskSnapshot ->
//            imageRef.downloadUrl.addOnSuccessListener { uri ->
//                callback(uri.toString())
//            }
//        }
//    }
}
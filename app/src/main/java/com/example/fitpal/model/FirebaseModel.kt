package com.example.fitpal.model

import android.graphics.Bitmap
import android.util.Log
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
                            Log.d("TAG", json.toString())
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

    //        database.collection(Constants.Collections.POSTS).document(post.id).set(post.json)
//            .addOnCompleteListener {
//                callback()
//            }
//            .addOnFailureListener {
//                Log.d("TAG", it.toString() + it.message)
//            }
    }

//    TODO implement delete
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
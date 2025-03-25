package com.example.fitpal.model


import com.example.fitpal.model.dao.AppLocalDbRepository
import com.example.fitpal.model.dao.AppLocalDB

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitpal.Comment
import com.example.fitpal.base.EmptyCallback
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    enum class Storage {
        FIREBASE,
        CLOUDINARY
    }

    private val database: AppLocalDbRepository = AppLocalDB.database
    private var executor = Executors.newSingleThreadExecutor()
    private val cloudinaryModel = CloudinaryModel()

    val posts: LiveData<List<Post>>
        get() = database.postDao().getAllPosts()

    val comments: LiveData<List<Comment>>
        get() = database.commentsDao().getAllComments()

    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()

    private val firebaseModel = FirebaseModel()
//    private val cloudinaryModel = CloudinaryModel() // TODO implement cloudinary

    companion object {
        val shared = Model()
    }

    fun refreshAllPosts() {
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated: Long = Post.lastUpdated
        firebaseModel.getAllPosts(lastUpdated) { posts ->
            executor.execute {
                // Clear table
                database.postDao().clearPostsTable()

                var currentTime = lastUpdated
                for (post in posts) {
                    Log.d("TAG", post.toString())
                    database.postDao().insertAll(post)
                    post.lastUpdated?.let {
                        if (currentTime < it) {
                            currentTime = it
                        }
                    }
                }

                Post.lastUpdated = currentTime
            }
        }
        loadingState.postValue(LoadingState.LOADED)
    }

    fun refreshComments(postId: String) {

        Log.d("TAG_refreshComments", "post: " + postId)
        firebaseModel.getPostById(postId) { post ->
            executor.execute {
                // Clear table
                database.commentsDao().clearComnmentsTable()

                Log.d("TAG_post", "post: " + post.toString())
                Log.d("TAG_post_comments", "comments: " + post?.comments.toString())
                if (post?.comments == null || post.comments.isEmpty()) {
                    return@execute
                }
                val commentsList = post.comments as List<*>
                for (i in commentsList.indices) {
                    try {
                        val item = commentsList[i]
                        Log.d("TAG_item_type", "Item type: " + (item?.javaClass?.name ?: "null"))

                        if (item is Map<*, *>) {
                            @Suppress("UNCHECKED_CAST")
                            val commentMap = item as Map<String, Any>

                            // Create a Comment from the map
                            val commentObj = Comment(
                                id = commentMap["id"] as? String ?: "",
                                author = commentMap["author"] as? String ?: "",
                                text = commentMap["text"] as? String ?: ""
                            )

                            // Log the created object
                            Log.d("TAG_comment_created", "Comment: $commentObj")

                            // Insert the created object
                            database.commentsDao().insertAll(commentObj)
                            Log.d("TAG_comment_inserted", "Comment inserted successfully")
                        } else {
                            Log.d("TAG_item_not_map", "Item is not a Map")
                        }
                    } catch (e: Exception) {
                        Log.e("TAG_error", "Error processing comment at index $i", e)
                        e.printStackTrace()
                    }
                }
//                        val comment = Comment.fromMap(commentMap as Map<String, Any>)
//                post?.comments?.forEach { commentMap ->
//                    try {
//                        Log.d("TAG_comment", "comment: " + commentMap.toString())
//                        // Convert HashMap to Comment
//                        // Insert into the database
//                        database.commentsDao().insertAll(comment)
//                    } catch (e: Exception) {
//                        Log.e("TAG", "Error converting comment", e)
//                    }
//                }
            }
        }
    }

    fun add(post: Post, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
        firebaseModel.add(
            post,
        ) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = post.id,
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val st = post.copy(image = uri)
                            firebaseModel.add(st, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    fun getPostById(postId: String, callback: (Post?) -> Unit) {
        firebaseModel.getPostById(postId, callback)
    }

    fun addComment(postId: String, comment: Comment, callback: (Boolean) -> Unit) {
        firebaseModel.addComment(postId, comment, callback)
    }

    fun like(postId: String, author: String, callback: EmptyCallback) {
        firebaseModel.like(postId, author, callback)
    }

    fun delete(post: Post, callback: EmptyCallback) {
        firebaseModel.delete(post, callback)
    }

    private fun uploadTo(storage: Storage, image: Bitmap, name: String, callback: (String?) -> Unit) {
        when (storage) {
            Storage.FIREBASE -> {
                uploadImageToFirebase(image, name, callback)
            }
            Storage.CLOUDINARY -> {
                uploadImageToCloudinary(
                    bitmap = image,
                    name = name,
                    onSuccess = callback,
                    onError = { callback(null) }
                )
            }
        }
    }

    private fun uploadImageToFirebase(
        image: Bitmap,
        name: String,
        callback: (String?) -> Unit
    ) {
        firebaseModel.uploadImage(image, name, callback)
    }

    private fun uploadImageToCloudinary(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        Log.d("TAG_uploadImageToCloudinary", name)
        cloudinaryModel.uploadImage(
            bitmap = bitmap,
            name = name,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}
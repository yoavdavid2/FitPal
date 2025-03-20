package com.example.fitpal.model


import android.R
import com.example.fitpal.model.dao.AppLocalDbRepository
import com.example.fitpal.model.dao.AppLocalDB

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val posts: LiveData<List<Post>>
        get() = database.postDao().getAllPosts()
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

    fun add(post: Post, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
        firebaseModel.add(
            post,
            callback = callback
        ) 
//        firebaseModel.add(post) {
//            image?.let {
//                uploadTo(
//                    storage,
//                    image = image,
//                    name = student.id,
//                    callback = { uri ->
//                        if (!uri.isNullOrBlank()) {
//                            val st = student.copy(avatarUrl = uri)
//                            firebaseModel.add(st, callback)
//                        } else {
//                            callback()
//                        }
//                    },
//                )
//            } ?: callback()
//        }
    }

    fun like(postId: String, author: String, callback: EmptyCallback) {
        firebaseModel.like(postId, author, callback)
    }

    fun delete(post: Post, callback: EmptyCallback) {
        firebaseModel.delete(post, callback)
    }

//    private fun uploadTo(storage: Storage, image: Bitmap, name: String, callback: (String?) -> Unit) {
//        when (storage) {
//            Storage.FIREBASE -> {
//                uploadImageToFirebase(image, name, callback)
//            }
//            Storage.CLOUDINARY -> {
//                uploadImageToCloudinary(
//                    bitmap = image,
//                    name = name,
//                    onSuccess = callback,
//                    onError = { callback(null) }
//                )
//            }
//        }
//    }


//    private fun uploadImageToFirebase(
//        image: Bitmap,
//        name: String,
//        callback: (String?) -> Unit
//    ) {
//        firebaseModel.uploadImage(image, name, callback)
//    }
//
//    private fun uploadImageToCloudinary(
//        bitmap: Bitmap,
//        name: String,
//        onSuccess: (String?) -> Unit,
//        onError: (String?) -> Unit
//    ) {
//        cloudinaryModel.uploadImage(
//            bitmap = bitmap,
//            name = name,
//            onSuccess = onSuccess,
//            onError = onError
//        )
//    }
}
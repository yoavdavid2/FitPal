package com.example.fitpal3.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal3.model.Model
import com.example.fitpal3.model.Post

class PostsListViewModel: ViewModel() {
    val posts: LiveData<List<Post>> = Model.Companion.shared.posts

    fun refreshAllPosts() {
        Model.Companion.shared.refreshAllPosts()
    }
}
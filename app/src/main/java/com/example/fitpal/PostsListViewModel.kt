package com.example.fitpal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post

class PostsListViewModel: ViewModel() {
    var posts: LiveData<List<Post>> = Model.shared.posts

    fun refreshAllPosts() {
        Model.shared.refreshAllPosts()
    }
}
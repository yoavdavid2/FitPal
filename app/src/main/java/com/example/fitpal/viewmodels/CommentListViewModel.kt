package com.example.fitpal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal.Comment
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post

class CommentListViewModel: ViewModel() {
    val comments: LiveData<List<Comment>> = Model.Companion.shared.comments

    fun refreshComments(postId: String) {
        Model.Companion.shared.refreshComments(postId)
    }
}
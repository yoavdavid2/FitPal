package com.example.fitpal.base

import com.example.fitpal.model.Post


typealias PostsCallback = (List<Post>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val POSTS = "posts"
    }
}
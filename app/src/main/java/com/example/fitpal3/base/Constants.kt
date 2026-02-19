package com.example.fitpal3.base

import com.example.fitpal3.model.Post


typealias PostsCallback = (List<Post>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val POSTS = "posts"
    }
}
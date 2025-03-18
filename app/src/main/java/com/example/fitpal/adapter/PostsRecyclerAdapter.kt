package com.example.fitpal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.PostListRowBinding
import com.example.fitpal.model.Post
import com.example.fitpal.OnItemClickListener


class PostsRecyclerAdapter(var posts: List<Post>?) : RecyclerView.Adapter<PostViewHolder>() {
    var listener: OnItemClickListener? = null

    fun update(posts: List<Post>?) {
//        val temp = this.posts?.toMutableList() ?: mutableListOf()
//
//        if (posts != null) {
//            temp.addAll(posts)
//        }
//
//        Log.d("TAG_RecyclerView_Update_temp", temp.toString())
//        this.posts = temp

//        this.posts = posts
        Log.d("TAG_RecyclerView_Update", this.posts.toString())

        if (posts != null) {
            this.posts = posts
        }
    }

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        Log.d("TAG_RecyclerView", this.posts.toString())
        val inflator = LayoutInflater.from(parent.context)
        val binding = PostListRowBinding.inflate(inflator, parent, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(
            post = posts?.get(position),
            position = position
        )
    }
}
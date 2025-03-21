package com.example.fitpal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.OnItemClickListener
import com.example.fitpal.databinding.PostListRowBinding
import com.example.fitpal.model.Post

class PostsRecyclerAdapter(var posts: List<Post>?) : RecyclerView.Adapter<PostViewHolder>() {
    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListRowBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(
            post = posts?.get(position),
            position = position,
            postId = posts?.get(position)?.id ?: ""
        )
    }
}
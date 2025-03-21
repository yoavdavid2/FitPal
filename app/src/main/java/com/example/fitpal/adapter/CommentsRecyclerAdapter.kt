package com.example.fitpal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.Comment
import com.example.fitpal.OnItemClickListener
import com.example.fitpal.databinding.CommentListRowBinding
import com.example.fitpal.databinding.PostListRowBinding
import com.example.fitpal.model.Post

class CommentsRecyclerAdapter (var comments: List<Comment>?) : RecyclerView.Adapter<CommentViewHolder>() {
    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int = comments?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentListRowBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(
            comment = comments?.get(position),
            position = position,
            postId = position.toString()
        )
    }
}
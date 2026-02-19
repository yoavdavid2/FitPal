package com.example.fitpal3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.Comment
import com.example.fitpal3.OnItemClickListener
import com.example.fitpal3.databinding.CommentListRowBinding

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
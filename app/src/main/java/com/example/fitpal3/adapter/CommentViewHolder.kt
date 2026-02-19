package com.example.fitpal3.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.Comment
import com.example.fitpal3.OnItemClickListener
import com.example.fitpal3.databinding.CommentListRowBinding


class CommentViewHolder(
    private val binding: CommentListRowBinding,
    listener: OnItemClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private var comment: Comment? = null

    fun bind(comment: Comment?,  position: Int, postId: String) {
        this.comment = comment
        binding.apply {
            author.text = comment?.author
            text.text = comment?.text
        }
    }
}
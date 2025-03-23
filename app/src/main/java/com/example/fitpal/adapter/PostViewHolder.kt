package com.example.fitpal.adapter

import android.util.Log
import android.view.View
import android.view.animation.ScaleAnimation
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.OnItemClickListener
import com.example.fitpal.R
import com.example.fitpal.databinding.PostListRowBinding
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post

class PostViewHolder(
    private val binding: PostListRowBinding,
    listener: OnItemClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private var post: Post? = null
    private var isLiked = false

    init {
        itemView.setOnClickListener {
            Log.d("TAG", "On click listener on position $adapterPosition")
            listener?.onItemClick(post)
        }

        binding.btnComment.setOnClickListener {
            listener?.onCommentClick(post?.id ?: "")
        }
    }

    fun bind(post: Post?, position: Int, postId: String) {
        this.post = post

        binding.authorImage.setImageResource(R.drawable.ic_profile_filled)
        binding.postTitle.text = post?.title
        binding.postText.text = post?.text
        binding.postDate.text = post?.uploadDate

        val likesCount = post?.likes?.size ?: 0
        val commentCount = post?.comments?.size ?: 0
        val author = "author" //TODO relace with the current user

        Log.d("TAG_likes", post?.likes.toString())
        Log.d("TAG_likes_contains_author", post?.likes?.contains(author).toString())
        isLiked = post?.likes?.contains(author) == true
        Log.d("TAG_likes_isLiked", isLiked.toString())

        binding.likesBadge.text = likesCount.toString()
        binding.commentsBadge.text = commentCount.toString()

        binding.btnLike.setOnClickListener {
            isLiked = !isLiked
            post?.let {
                it.likes = if (isLiked) it.likes + author else it.likes - author
                Model.shared.like(it.id, author, callback = {})
                updateLikesDisplay()
                animateLikeButton()
            }
        }

    }

    private fun updateLikesDisplay() {
        val likesCount = post?.likes?.size ?: 0
        binding.likesBadge.visibility = View.VISIBLE
        binding.likesBadge.text = likesCount.toString()
        binding.btnLike.setImageResource(
            if (isLiked) R.drawable.ic_like else R.drawable.ic_like_outlined
        )
    }

    private fun animateLikeButton() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.2f,
            1.0f, 1.2f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 200
        scaleAnimation.fillAfter = true
        binding.btnLike.startAnimation(scaleAnimation)
    }
}
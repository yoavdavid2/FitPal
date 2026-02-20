package com.example.fitpal3.adapter

import android.util.Log
import android.view.View
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.OnItemClickListener
import com.example.fitpal3.R
import com.example.fitpal3.databinding.PostListRowBinding
import com.example.fitpal3.model.Model
import com.example.fitpal3.model.Post
import com.squareup.picasso.Picasso
import kotlin.text.isNotBlank

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

        Picasso.get().cancelRequest(binding.postImage)
        binding.postImage.setImageDrawable(null)

        this.post?.image?.let {
            if (it.isNotBlank() || it != "") {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.postImage)
            } else {
                R.drawable.ic_launcher_background
            }
        }

        val likesCount = post?.likes?.size ?: 0
        val commentCount = post?.comments?.size ?: 0
        val author = "author" //TODO relace with the current user

        isLiked = post?.likes?.contains(author) == true

        binding.likesBadge.text = likesCount.toString()
        binding.commentsBadge.text = commentCount.toString()

        binding.btnLike.setImageResource(
            if (isLiked) R.drawable.ic_like else R.drawable.ic_like_outlined
        )

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
package com.example.fitpal.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.OnItemClickListener
import com.example.fitpal.R
import com.example.fitpal.databinding.PostListRowBinding
import com.example.fitpal.model.Post
import com.squareup.picasso.Picasso


class PostViewHolder (
    private val binding: PostListRowBinding,
    listener: OnItemClickListener?
): RecyclerView.ViewHolder(binding.root) {

    private var post: Post? = null

//    init {
//        binding.checkBox.apply {
//            setOnClickListener {
//                (tag as? Int)?.let { tag ->
//                    post?.isChecked = (it as? CheckBox)?.isChecked ?: false
//                }
//            }
//        }
//
//        itemView.setOnClickListener {
//            Log.d("TAG", "On click listener on position $adapterPosition")
////                listener?.onItemClick(adapterPosition)
//            listener?.onItemClick(post)
//        }
//    }

    fun bind(post: Post?, position: Int) {

        binding.authorImage.setImageResource(R.drawable.ic_profile_filled)
        binding.postTitle.text = post?.title
        binding.postText.text = post?.text
        binding.likeCount.text = post?.likes.toString()
        binding.commentCount.text = post?.comments.toString()
        binding.postDate.text = post?.uploadDate // Display the post date


        binding.btnLike.setOnClickListener {
            post?.likes = post.likes.plus(1)
            binding.likeCount.text = post?.likes.toString()
        }

        binding.btnComment.setOnClickListener {
            // Handle comment button click
        }

        post?.image?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_profile_filled)
                    .into(binding.postImage)
            }
        }
    }
}
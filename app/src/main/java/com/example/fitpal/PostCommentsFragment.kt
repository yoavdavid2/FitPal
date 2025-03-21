package com.example.fitpal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.adapter.CommentsRecyclerAdapter
import com.example.fitpal.adapter.PostsRecyclerAdapter
import com.example.fitpal.databinding.FragmentFeedBinding
import com.example.fitpal.databinding.FragmentPostCommentsBinding
import com.example.fitpal.model.Model
import com.example.fitpal.viewmodels.CommentListViewModel
import com.example.fitpal.viewmodels.PostsListViewModel
import java.util.UUID
import kotlin.getValue


private const val ARG_PARAM_POST_ID = "postId"

class PostCommentsFragment : Fragment() {

    private var adapter: CommentsRecyclerAdapter? = null
    private val viewModel: CommentListViewModel by viewModels()

    private var postId: String? = null
    private var binding: FragmentPostCommentsBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(ARG_PARAM_POST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCommentsBinding.inflate(inflater, container, false)
        binding?.toolbar?.setNavigationOnClickListener(::onCancelClick)

        val commentsRecyclerView: RecyclerView? = binding?.commentsRecyclerView
        commentsRecyclerView?.setHasFixedSize(true)


        Model.shared.getPostById(postId!!) { post ->
//            binding?.imageView = ''
            binding?.toolbar?.title = "Comments on " + post?.title
            binding?.postDate?.text = post?.uploadDate
            binding?.postText?.text = post?.text
            binding?.postAuthor?.text = post?.author
        }

        getCommentsOnPosts(postId!!)

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            adapter?.comments = comments
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
            if (comments.isEmpty()) {
                binding?.noCommets?.visibility = View.VISIBLE
            } else {
                binding?.noCommets?.visibility = View.GONE
            }
        }

        binding?.saveButton?.setOnClickListener {
            val newComment = binding?.newComment?.text.toString()
            val CONST_AUTHOR = "author" //TODO get user Email
            val uuid: String = UUID.randomUUID().toString()
            val c = Comment(uuid, CONST_AUTHOR, newComment)
            Model.shared.addComment(postId!!, c, callback = {})
            binding?.newComment?.setText("")
        }

        return binding?.root
    }

    private fun onCancelClick(view: View) {
        Navigation.findNavController(view).popBackStack()
    }

    private fun getCommentsOnPosts(postId: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        Model.shared.refreshComments(postId)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostCommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_POST_ID, param1)
                }
            }
    }
}
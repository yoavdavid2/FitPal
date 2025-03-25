package com.example.fitpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.adapter.CommentsRecyclerAdapter
import com.example.fitpal.databinding.FragmentPostCommentsBinding
import com.example.fitpal.model.Model
import com.example.fitpal.viewmodels.CommentListViewModel
import java.util.UUID


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
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPostCommentsBinding.inflate(inflater, container, false)

        val commentsRecyclerView: RecyclerView? = binding?.commentsRecyclerView
        commentsRecyclerView?.setHasFixedSize(true)
        commentsRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

        adapter = CommentsRecyclerAdapter(viewModel.comments.value)
        commentsRecyclerView?.adapter = adapter

        getCommentsOnPosts(postId.toString())

        Model.shared.getPostById(postId!!) { post ->
            binding?.apply {
                postDate.text = post?.uploadDate
                postText.text = post?.text
                postAuthor.text = post?.author
            }
        }

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            adapter?.comments = comments
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
            if (comments.isEmpty()) {
                binding?.noComments?.visibility = View.VISIBLE
            } else {
                binding?.noComments?.visibility = View.GONE
            }
        }

        binding?.apply {
            saveButton.setOnClickListener {
                val newComment = binding?.newComment?.text.toString()
                val CONST_AUTHOR = "author" //TODO get user Email
                val uuid: String = UUID.randomUUID().toString()
                val c = Comment(uuid, CONST_AUTHOR, newComment)
                Model.shared.addComment(postId!!, c) { success ->
                    if (success) {
                        viewModel.refreshComments(postId!!) {

                        }
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
                binding?.newComment?.setText("")
            }

            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            swipeRefreshLayout.setOnRefreshListener {
                getCommentsOnPosts(postId!!)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding?.root
    }

    private fun onCancelClick(view: View) {
        Navigation.findNavController(view).popBackStack()
    }

    private fun getCommentsOnPosts(postId: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.refreshComments(postId) {
            binding?.progressBar?.visibility = View.GONE
        }
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
package com.example.fitpal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.adapter.PostsRecyclerAdapter
import com.example.fitpal.databinding.FragmentFeedBinding
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post
import com.example.fitpal.viewmodels.PostsListViewModel


class FeedFragment : Fragment() {

    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentFeedBinding? = null

    private val viewModel: PostsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        getAllPosts()

        val recyclerView: RecyclerView? = binding?.recyclerView
        recyclerView?.setHasFixedSize(true)

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            viewModel.refreshAllPosts()
        }

        Model.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }

        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        adapter =  PostsRecyclerAdapter(viewModel.posts.value)
        recyclerView?.adapter = adapter

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
                post?.let {
                    Log.d("TAG", "On click Activity listener on post $post")
                }
            }
        }

        binding?.addPostButton?.setOnClickListener {
            Log.d("TAG", "Button clicked!")
            val action = FeedFragmentDirections.actionFeedFragmentToNewPostFragment()
            findNavController().navigate(action)
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
//        getAllPosts()
    }

    private fun getAllPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        Model.shared.refreshAllPosts()
    }
}
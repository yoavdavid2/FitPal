package com.example.fitpal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpal.adapter.PostsRecyclerAdapter
import com.example.fitpal.databinding.FragmentFeedBinding
import com.example.fitpal.model.Model
import com.example.fitpal.model.Post


class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null
    private var adapter: PostsRecyclerAdapter? = null

    private val viewModel: PostsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.update(it)
//            adapter?.posts = it
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }

        Model.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            viewModel.refreshAllPosts()
        }

        val layoutManager = LinearLayoutManager(context)
        adapter =  PostsRecyclerAdapter(viewModel.posts.value)

        binding?.recyclerView?.layoutManager = layoutManager

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
                post?.let {
                    Log.d("TAG", "On click Activity listener on post $post")
//                    val action = PostListFragmentDirections.actionStudentsListFragmentToBlueFragment(it.name)
//                    binding?.root?.let {
//                        Navigation.findNavController(it).navigate(action)
//                    }
                }
            }
        }
        binding?.recyclerView?.adapter = adapter

//        TODO("Fix PostListFragmentDirections")
//        val action = PostListFragmentDirections.actionGlobalAddStudentFragment()
//        binding?.addPostButton?.setOnClickListener(Navigation.createNavigateOnClickListener(action))

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
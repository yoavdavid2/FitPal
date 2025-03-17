package com.example.fitpal

import PostAdapter
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpal.databinding.FragmentFeedBinding
import com.example.fitpal.model.Post
import com.example.fitpal.viewmodels.FeedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null

    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        loadPosts()
        adapter = PostAdapter(postList)
        binding?.recyclerView?.adapter = adapter
    }

    private fun loadPosts() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())


        postList.add(Post(1, "Alice", "Hello world!", "Hello world!", R.drawable.ic_profile_filled, 10, 5, currentDate))
        postList.add(Post(1, "Bob", "Beautiful sunset!", "Hi everyone",R.drawable.ic_profile_filled, 25,4, currentDate))
        postList.add(Post(1, "Alice", "Hello world!", "Hello world!", R.drawable.ic_profile_filled, 10, 5, currentDate))
        postList.add(Post(1, "Bob", "Beautiful sunset!", "Hi everyone",R.drawable.ic_profile_filled, 25,4, currentDate))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
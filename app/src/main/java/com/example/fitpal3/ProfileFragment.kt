package com.example.fitpal3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.adapter.PostsRecyclerAdapter
import com.example.fitpal3.databinding.FragmentProfileBinding
import com.example.fitpal3.model.Model
import com.example.fitpal3.model.Post
import com.example.fitpal3.viewmodels.PostsListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private var adapter: PostsRecyclerAdapter? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: PostsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        //getAllPosts()
        getUserPosts() // New
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        binding?.logoutButton?.setOnClickListener {
            logoutUser()
        }


        fetchUserData()
        setupRecyclerView()
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firstName = document.getString("firstName") ?: "User"
                        val lastName = document.getString("lastName") ?: "Name"
                        val username = "$firstName $lastName"
                        val points = document.getLong("fp") ?: 0
                        val sports = document.get("sports") as? List<String> ?: emptyList()

                        binding?.userName?.text = username

                        setupSportsLabels(sports)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupSportsLabels(sports: List<String>) {
        val parentLayout = binding?.sportsFlow?.parent as ViewGroup

        for (id in binding?.sportsFlow!!.referencedIds) {
            parentLayout.findViewById<View>(id)?.let { parentLayout.removeView(it) }
        }

        val ids = mutableListOf<Int>()

        for (sport in sports) {
            val label = LayoutInflater.from(requireContext()).inflate(
                R.layout.sport_label, parentLayout, false
            ) as AppCompatTextView

            label.text = sport
            label.id = View.generateViewId()

            parentLayout.addView(label)
            ids.add(label.id)
        }

        binding?.sportsFlow!!.referencedIds = ids.toIntArray()
    }

    private fun setupRecyclerView() {


        val recyclerView: RecyclerView? = binding?.postsRecyclerView
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

            override fun onCommentClick(postId: String) {
                println("Comment clicked: $postId")
            }
        }
    }

    private fun logoutUser() {
        auth.signOut()

        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToLoginFragment(),
            NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun getAllPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        Model.shared.refreshAllPosts()
    }

    private fun getAuthor( callback: (String) -> Unit) {
        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        var author: String = "**************temp-author**************"
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        author = document.getString("email") ?: ""
                        callback(author)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getUserPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        getAuthor() { author ->
            Model.shared.getPostByEmail(
                author,
                callback = {
                    binding?.progressBar?.visibility = View.GONE
                }
            )
        }
    }
}

package com.example.fitpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitpal.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        binding.logoutButton.setOnClickListener {
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

                        binding.userName.text = username
                        binding.fpBadge.text = "$points FP"

                        setupSportsLabels(sports)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupSportsLabels(sports: List<String>) {
        val parentLayout = binding.sportsFlow.parent as ViewGroup

        for (id in binding.sportsFlow.referencedIds) {
            parentLayout.findViewById<View>(id)?.let { parentLayout.removeView(it) }
        }

        val ids = mutableListOf<Int>()

        for (sport in sports) {
            val label = LayoutInflater.from(requireContext()).inflate(
                R.layout.sport_label, parentLayout, false
            ) as androidx.appcompat.widget.AppCompatTextView

            label.text = sport
            label.id = View.generateViewId()

            parentLayout.addView(label)
            ids.add(label.id)
        }

        binding.sportsFlow.referencedIds = ids.toIntArray()
    }

    private fun setupRecyclerView() {
//        val posts = listOf(
//            Post("Post title", "Post description and something like that"),
//            Post("Post title", "Post description and something like that"),
//            Post("Post title", "Post description and something like that")
//        )
//
//        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.postsRecyclerView.adapter = PostsAdapter(posts)
    }

    private fun logoutUser() {
        auth.signOut()

        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToLoginFragment(),
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

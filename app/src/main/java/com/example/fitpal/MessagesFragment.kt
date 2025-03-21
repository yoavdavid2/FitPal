package com.example.fitpal

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.adapter.ChatsRecyclerAdapter
import com.example.fitpal.adapter.OnItemChatClickListener
import com.example.fitpal.databinding.FragmentMessagesBinding
import com.example.fitpal.model.Chat
import com.example.fitpal.model.FirebaseModel
import com.example.fitpal.model.Model
import com.example.fitpal.viewmodels.ChatsViewModel
import com.google.firebase.auth.FirebaseAuth

class MessagesFragment : Fragment() {
    private var binding: FragmentMessagesBinding? = null
    private var recycleView: RecyclerView? = null
    private val viewModel: ChatsViewModel by viewModels()
    private lateinit var adapter: ChatsRecyclerAdapter
    private lateinit var firebaseModel: FirebaseModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        Log.d(TAG, "Message Fragment created")
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseModel = FirebaseModel()
        firebaseAuth= FirebaseAuth.getInstance()

        recycleView = binding?.recyclerView
        recycleView?.layoutManager = LinearLayoutManager(requireContext())

        fetchChats()

        adapter = ChatsRecyclerAdapter()
        recycleView?.adapter = adapter


        observeViewModel()
        addButtonsListeners()
    }

    private fun addButtonsListeners() {
        binding?.addChat?.setOnClickListener {
            binding?.addChatSection?.visibility =
                if (binding?.addChatSection?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        binding?.addUserToInboxButton?.setOnClickListener {
            val email = binding?.addedUsernameChatTextField?.editText?.text?.toString()?.trim()

            val myUser = firebaseAuth.currentUser?.email

            if (!email.isNullOrEmpty() && !myUser.isNullOrEmpty()) {
                val newChat = Chat(
                    chatUsers = listOf(myUser, email),
                    messages = listOf(),
                    lastUpdated = System.currentTimeMillis(),
                    id = "temp"
                )

                binding?.addChatSection?.visibility = View.GONE

                firebaseModel.addChat(newChat) { success ->
                    if (success) {
                        hideKeyboard(binding?.textInput)
                        viewModel.addChat(newChat)
                        firebaseAuth.currentUser?.email?.let { it1 -> viewModel.updateChats(it1) }
                    } else {
                        Toast.makeText(requireContext(), "Failed to add chat", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentChats.observe(viewLifecycleOwner) { chats ->
            if (!chats.isNullOrEmpty()) {
                binding?.newChatterName?.visibility = View.GONE
                adapter.update(chats)
            } else {
                binding?.newChatterName?.visibility = View.VISIBLE

            }
        }
    }

    private fun fetchChats() {
        firebaseAuth.currentUser?.email?.let { Model.shared.refreshAllChats(it) }
    }

    private fun hideKeyboard(editText: EditText?) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText?.windowToken, 0)
    }

    companion object {
        private const val TAG = "MessagesFragment"
    }
}

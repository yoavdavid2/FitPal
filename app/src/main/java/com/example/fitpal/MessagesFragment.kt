package com.example.fitpal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.FragmentMessagesBinding
import com.example.fitpal.model.Chat
import com.example.fitpal.model.FirebaseModel
import com.example.fitpal.model.adapters_viewholders.ChatsRecyclerAdapter
import com.example.fitpal.model.adapters_viewholders.OnItemChatClickListener
import com.example.fitpal.viewmodels.ChatsViewModel
import com.google.firebase.FirebaseApp


class MessagesFragment : Fragment(), OnItemChatClickListener {
    private var binding: FragmentMessagesBinding? = null
    private var recycleView: RecyclerView? = null
    private val viewModel: ChatsViewModel by viewModels()
    private var adapter: ChatsRecyclerAdapter? = null
    private lateinit var firebaseModel: FirebaseModel

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

        recycleView = binding?.recyclerView

        val layoutManager = LinearLayoutManager(requireContext())
        recycleView?.layoutManager = layoutManager

        adapter = ChatsRecyclerAdapter(viewModel.currentChats.value)

        observeViewModel()

        binding?.addChat?.setOnClickListener {
            binding?.addChatSection?.visibility = if (binding?.addChatSection?.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding?.addUserToInboxButton?.setOnClickListener {
            val email = binding?.addedUsernameChatTextField?.editText?.text?.toString()

            if (!email.isNullOrEmpty()) {

                val newChat = Chat(
                    chatUsers = listOf("me", email),
                    messages = listOf(),
                )

                firebaseModel.addChat(newChat, {})

            } else {
                Toast.makeText(it.context, "Please fill the other person's username", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observeViewModel() {
        viewModel.apply {
            currentChats.observe(viewLifecycleOwner) { chats ->
                if (!chats.isNullOrEmpty()) {
                    binding?.newChatterName?.visibility = View.GONE
                    updateChats(chats)
                } else {
                    binding?.newChatterName?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateChats(chats: List<Chat>) {
        val adapter = ChatsRecyclerAdapter(chats)
        adapter.setOnItemChatClickListener(this)
        recycleView?.adapter = adapter
    }

    override fun onChatClick(chatDetails: Chat?) {
        chatDetails?.let {
            val action = MessagesFragmentDirections.actionMessagesFragmentToChatFragment(it.chatUsers[1]) //Replace not correct!
            findNavController().navigate(action)
        }
    }

    companion object {
        private const val TAG = "messageFragment"
    }
}
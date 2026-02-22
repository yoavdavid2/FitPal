package com.example.fitpal3

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.adapter.messagesInChatRecyclerAdapter
import com.example.fitpal3.databinding.FragmentChatBinding
import com.example.fitpal3.model.FirebaseModel
import com.example.fitpal3.model.Message
import com.example.fitpal3.viewmodels.MessagesInChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID


class ChatFragment : Fragment() {
    private var binding: FragmentChatBinding? = null
    private var recycleView: RecyclerView? = null
    private var username: String? = null
    private var chatId: String? = null
    private val viewModel: MessagesInChatViewModel by viewModels()
    private lateinit var firebaseModel: FirebaseModel
    private lateinit var adapter: messagesInChatRecyclerAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        username = ChatFragmentArgs.fromBundle(requireArguments()).chatterUsername
        chatId = ChatFragmentArgs.fromBundle(requireArguments()).chatId
                firebaseAuth = FirebaseAuth.getInstance()
                setUserName()
                setOnReturnButtonClick()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseModel = FirebaseModel()

        recycleView = binding?.messagesRecyclerView
        recycleView?.layoutManager = LinearLayoutManager(requireContext())

        adapter = messagesInChatRecyclerAdapter()
        recycleView?.adapter = adapter

        chatId?.let { fetchChatMessages(it) }
        setButtonsListeners()
        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.currentMessages.observe(viewLifecycleOwner) { messages ->
            adapter.update(messages)
        }
    }


//    private fun fetchChatMessages(chatId: String) {
//        firebaseModel.getChatMessages(chatId) { messages: MutableList<Message> ->
//            viewModel.updateMessages(messages)
//        }
//    }

    private fun fetchChatMessages(chatId: String) {
        firebaseModel.listenToChatMessages(chatId) { messages ->
            viewModel.updateMessages(messages)
        }
    }

    private fun setButtonsListeners() {
        binding?.sendButton?.setOnClickListener {
            val userInputMessage = binding?.messageInput?.text.toString()

            chatId?.let {
                if (!userInputMessage.isNullOrEmpty()) {
                    binding?.messageInput?.setText("");
                    hideKeyboard(binding?.messageInput)

                    val newMessage = Message( id = chatId + "_" + UUID.randomUUID().toString(),
                        messageText = userInputMessage,
                        senderId = firebaseAuth.currentUser?.email ?: "me",
                        timestamp =com.google.firebase.Timestamp.now()
                    )

                    firebaseModel.addNewMessage(newMessage, it) { success ->
                        if (success) {
//                            viewModel.addNewMessage(newMessage)
                        } else {
                            Toast.makeText(requireContext(), "Failed to add chat", Toast.LENGTH_SHORT).show()
                        } }
                }
            }
        }
    }

    private fun setOnReturnButtonClick() {
        val backButton: ImageButton? = binding?.backButton
        backButton?.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setUserName() {
        binding?.chatName?.text = username
    }

    private fun hideKeyboard(editText: EditText?) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText?.windowToken, 0)
    }

    override fun onStop() {
        firebaseModel.stopListeningToChatMessages()
        super.onStop()
    }
}

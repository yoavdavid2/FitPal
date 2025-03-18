package com.example.fitpal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.FragmentMessagesBinding
import com.example.fitpal.model.Chat
import com.example.fitpal.model.adapters_viewholders.ChatsRecyclerAdapter
import com.example.fitpal.model.adapters_viewholders.OnItemChatClickListener


class MessagesFragment : Fragment(), OnItemChatClickListener {
    private var binding: FragmentMessagesBinding? = null
    private var recycleView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)

        recycleView = binding?.recyclerView

        val layoutManager = LinearLayoutManager(requireContext())
        recycleView?.layoutManager = layoutManager

        val chats = this.getLastChats()
        val adapter = ChatsRecyclerAdapter(chats)

        adapter.setOnItemChatClickListener(this)

        recycleView?.adapter = adapter

        return binding?.root
    }

    private fun getLastChats(): ArrayList<Chat>{
        val chatList = ArrayList<Chat>()

        val chat1 = Chat("Ido", "hi")
        val chat2 = Chat("Gal", "bye")


        chatList.add(chat1)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat1)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)
        chatList.add(chat2)

        return chatList
    }

    override fun onChatClick(chatDetails: Chat) {
        val action = MessagesFragmentDirections.actionMessagesFragmentToChatFragment(chatDetails.username)
        findNavController().navigate(action)
    }

}
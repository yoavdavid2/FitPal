package com.example.fitpal3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemAvailableChatBinding
import com.example.fitpal3.model.Chat
import com.google.firebase.auth.FirebaseAuth


interface OnItemChatClickListener {
    fun onChatClick(chatDetails: Chat?)
}

class ChatsRecyclerAdapter : RecyclerView.Adapter<AvailableChatViewHolder>() {

    private val chats = mutableListOf<Chat>()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var listener: OnItemChatClickListener? = null

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: AvailableChatViewHolder, position: Int) {
        firebaseAuth.currentUser?.email?.let {
            holder.bind(
                chat = chats[position],
                currentUserName = it
            )
        }
    }

    fun update(newChats: List<Chat>?) {
        chats?.clear()
        if (newChats != null) {
            chats?.addAll(newChats.toMutableList())
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableChatViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val binding = ItemAvailableChatBinding.inflate(inflator, parent, false)
        return AvailableChatViewHolder(binding, listener)
    }


    fun setOnItemChatClickListener(listener: OnItemChatClickListener) {
        this.listener = listener
    }
}
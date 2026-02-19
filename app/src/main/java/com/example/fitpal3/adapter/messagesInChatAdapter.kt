package com.example.fitpal3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemChatMessageBinding
import com.example.fitpal3.model.Message
import com.google.firebase.auth.FirebaseAuth


class messagesInChatRecyclerAdapter : RecyclerView.Adapter<MessagesInChatViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessagesInChatViewHolder, position: Int) {
        firebaseAuth.currentUser?.email?.let {
            holder.bind(
                message = messages[position],
                currentUserEmail = it
            )
        }
    }

        fun update(newMessages: MutableList<Message>?) {
        messages.clear()
        if (newMessages != null) {
            messages.addAll(newMessages)
        }
        notifyDataSetChanged()
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesInChatViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val binding = ItemChatMessageBinding.inflate(inflator, parent, false)
        return MessagesInChatViewHolder(binding)
    }
}

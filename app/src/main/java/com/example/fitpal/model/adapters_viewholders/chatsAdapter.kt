package com.example.fitpal.model.adapters_viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.R
import com.example.fitpal.model.Chat


interface OnItemChatClickListener {
    fun onChatClick(chatDetails: Chat)
}

class ChatsRecyclerAdapter(private val data: List<Chat>) : RecyclerView.Adapter<AvailableChatViewHolder>() {

    private var listener: OnItemChatClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_available_chat, parent, false)
        return AvailableChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvailableChatViewHolder, position: Int) {
        val currentChat = data[position]

        holder.username?.text = currentChat.username
        holder.lastMessage?.text = currentChat.lastMessage

        holder.itemView.setOnClickListener {
            listener?.onChatClick(currentChat)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemChatClickListener(listener: OnItemChatClickListener) {
        this.listener = listener
    }
}
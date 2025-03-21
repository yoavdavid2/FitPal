package com.example.fitpal.model.adapters_viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.R
import com.example.fitpal.model.Chat
import com.example.fitpal.model.Message


interface OnItemChatClickListener {
    fun onChatClick(chatDetails: Chat?)
}

class ChatsRecyclerAdapter(private var data: List<Chat>?) : RecyclerView.Adapter<AvailableChatViewHolder>() {

    private var listener: OnItemChatClickListener? = null

    fun update(chats: List<Chat>?) {
        chats?.let {
            this.data = it
        }
    }

    override fun getItemCount(): Int = data?.size ?: 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_available_chat, parent, false)
        return AvailableChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvailableChatViewHolder, position: Int) {
        val currentChat = data?.get(position)

        currentChat?.let {

            holder.username?.text =dislpayOtherChatterUser(it.chatUsers)
            holder.lastMessage?.text = getLastMessageText(it.messages)

            holder.itemView.setOnClickListener {
                listener?.onChatClick(currentChat)
            }
        }
    }


    fun setOnItemChatClickListener(listener: OnItemChatClickListener) {
        this.listener = listener
    }

    private fun dislpayOtherChatterUser(chatters: List<String>): String {
        // Todo: filter our from the list the active user username
        return "me"
    }

    private fun getLastMessageText(messages: List<Message>): String {
        return messages.last().messageText
    }
}
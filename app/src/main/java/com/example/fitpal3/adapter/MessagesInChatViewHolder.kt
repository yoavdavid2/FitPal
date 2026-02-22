package com.example.fitpal3.adapter

import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal3.databinding.ItemChatMessageBinding
import com.example.fitpal3.model.Message

class MessagesInChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message, currentUserEmail: String) {
        binding.apply {
            if (message.senderId == currentUserEmail) {
                binding.messageText.text = message.messageText
                binding.senderEmail.text = "me"
                binding.messageText.gravity = Gravity.START
                binding.senderEmail.gravity = Gravity.START
            } else {
                binding.messageText.text = message.messageText
                binding.senderEmail.text =  message.senderId
                binding.messageText.gravity = Gravity.END
                binding.senderEmail.gravity = Gravity.END
            }
        }
    }
}

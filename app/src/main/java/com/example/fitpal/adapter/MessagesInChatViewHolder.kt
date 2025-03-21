package com.example.fitpal.adapter

import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.databinding.ItemChatMessageBinding
import com.example.fitpal.model.Message

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
                binding.senderEmail.text = currentUserEmail
                binding.messageText.gravity = Gravity.END
                binding.senderEmail.gravity = Gravity.END
            }
        }
    }
}

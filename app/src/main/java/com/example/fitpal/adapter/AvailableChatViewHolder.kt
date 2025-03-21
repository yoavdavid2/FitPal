package com.example.fitpal.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController

import androidx.navigation.fragment.findNavController
import com.example.fitpal.databinding.ItemAvailableChatBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.FeedFragmentDirections
import com.example.fitpal.MessagesFragmentDirections
import com.example.fitpal.OnItemClickListener
import com.example.fitpal.R
import com.example.fitpal.model.Chat
import com.example.fitpal.model.Message

class AvailableChatViewHolder(private val binding: ItemAvailableChatBinding,  listener: OnItemChatClickListener?) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chat: Chat, currentUserName: String) {
        binding.apply {
            if (chat.messages.isEmpty()) {
                chatLastMessage.text = "empty chat"
            } else {
                chatLastMessage.text = chat.messages.last().messageText
            }

            chatName.text = getOtherUser(chat.chatUsers, currentUserName)
            chatAvatar.setImageResource(R.drawable.avatar)

            root.setOnClickListener{

                val action = MessagesFragmentDirections.actionMessagesFragmentToChatFragment(chat.chatUsers[1], chat.id)
                it.findNavController().navigate(action)
            }
        }
    }

    fun getOtherUser(chatUsers: List<String>, currentUserEmail: String): String? {
        return chatUsers.firstOrNull { it != currentUserEmail }
    }

    private fun convertHashMapToMessage(messageMap: HashMap<String, Any>): Message? {
        val id = messageMap["id"] as? String
        val messageText = messageMap["messageText"] as? String
        val senderId = messageMap["senderId"] as? String
        val timestamp = messageMap["timestamp"] as? com.google.firebase.Timestamp

        if (id != null && messageText != null && senderId != null && timestamp != null) {
            return Message(id, messageText, senderId, timestamp)
        } else {
            return null
        }
    }
}

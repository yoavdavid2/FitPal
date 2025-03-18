package com.example.fitpal.model.adapters_viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.R

class AvailableChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var avatar: ImageView? = null
    var username: TextView? = null
    var lastMessage: TextView? = null

    init {
        avatar = itemView.findViewById<ImageView>(R.id.chat_avatar)
        username = itemView.findViewById<TextView>(R.id.chat_name)
        lastMessage = itemView.findViewById<TextView>(R.id.chat_last_message)
    }
}

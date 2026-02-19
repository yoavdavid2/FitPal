package com.example.fitpal3.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal3.model.Chat
import com.example.fitpal3.model.Model

class ChatsViewModel : ViewModel() {

    val currentChats: LiveData<List<Chat>> = Model.shared.chats


    fun updateChats(currentUserEmail: String) {
        Model.shared.refreshAllChats(currentUserEmail)
    }

    fun addChat(newChat: Chat) {
        Model.shared.addChat(newChat){}
    }
}
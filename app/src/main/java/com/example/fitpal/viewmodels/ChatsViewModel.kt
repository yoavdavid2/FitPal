package com.example.fitpal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal.model.Chat

class ChatsViewModel : ViewModel() {

    private val _currentChats = MutableLiveData<List<Chat>>()
    val currentChats: LiveData<List<Chat>> = _currentChats


    init {
        _currentChats.value = emptyList() // add Checking for room for locally saved chats
    }

    fun fetchChatsForUser(username: String) {
        val k = 0
    }
}
package com.example.fitpal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitpal.model.Chat
import com.example.fitpal.model.Message

class MessagesInChatViewModel : ViewModel() {
    private val _currentMessages = MutableLiveData<MutableList<Message>>()
    val currentMessages: LiveData<MutableList<Message>> = _currentMessages

    init {
        _currentMessages.value = mutableListOf()
    }

    fun updateMessages(updatedMessages: MutableList<Message>) {
        _currentMessages.postValue(updatedMessages)
    }

    fun addNewMessage(newMessage: Message) {
        val updatedMessages = _currentMessages.value?.toMutableList() ?: mutableListOf()
        updatedMessages.add(newMessage)
        _currentMessages.postValue(updatedMessages)
    }
}
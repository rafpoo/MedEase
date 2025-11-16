package com.example.medease.ui.appointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ChatRoomViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    // Ambil chatId dari arguments
    private val chatId: String = state["chatId"] ?: ""

    // Key unik per chatId supaya chat room tidak bercampur
    private val key = "messages_$chatId"

    // LiveData chat per chatId
    private val _messages = state.getLiveData<List<ChatMessage>>(key, emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val updated = (_messages.value ?: emptyList()).toMutableList().apply {
            add(ChatMessage(message = text, isSender = true, timestamp = System.currentTimeMillis()))
        }
        state[key] = updated
        _messages.value = updated
    }

    fun receiveMessage(text: String) {
        val updated = (_messages.value ?: emptyList()).toMutableList().apply {
            add(ChatMessage(message = text, isSender = false, timestamp = System.currentTimeMillis()))
        }
        state[key] = updated
        _messages.value = updated
    }
}

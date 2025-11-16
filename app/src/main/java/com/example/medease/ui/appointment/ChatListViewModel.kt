package com.example.medease.ui.appointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatListViewModel : ViewModel() {

    private val _chatList = MutableLiveData<List<ChatItem>>()
    val chatList: LiveData<List<ChatItem>> get() = _chatList

    init {
        // Dummy data, nanti bisa kamu ganti API/DB
        _chatList.value = listOf(
            ChatItem("1", "Doctor Bryan", "Sama-sama dok", "10:42"),
            ChatItem("2", "Dr. Adelia", "Baik, saya jelaskan ya...", "09:15"),
            ChatItem("3", "Patient Kevin", "Terima kasih dok...", "Kemarin")
        )
    }
}

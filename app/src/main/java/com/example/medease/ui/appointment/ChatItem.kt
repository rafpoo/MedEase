package com.example.medease.ui.appointment

data class ChatItem(
    val id: String,             // id percakapan/pasien
    val name: String,           // nama pasien/dokter
    val lastMessage: String,
    val lastTime: String
)

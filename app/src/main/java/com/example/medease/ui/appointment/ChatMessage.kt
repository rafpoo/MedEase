package com.example.medease.ui.appointment

data class ChatMessage(
    val message: String,
    val isSender: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)


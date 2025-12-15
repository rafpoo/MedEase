package com.example.medease.data.model

data class Appointment(
    val id: String = "",
    val userId: String = "",

    val doctorId: String = "",
    val doctorName: String = "",

    val category: String = "",
    val date: String = "",
    val time: String = "",
    val note: String = "",
    val status: String = "pending"
)


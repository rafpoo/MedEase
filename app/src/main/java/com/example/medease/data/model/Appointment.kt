package com.example.medease.data.model

data class Appointment(
    val doctorName: String,
    val category: String,
    val date: String,
    val time: String,
    val note: String
)

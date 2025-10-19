package com.example.medease.data.model

data class Appointment(
    var id: Int = 0,
    val doctor: String,
    val category: String,
    val date: String,
    val time: String,
    val note: String
)


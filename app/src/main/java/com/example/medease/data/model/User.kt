package com.example.medease.data.model

data class User(
    val nama: String? = "",
    val email: String? = "",
    val appointments: List<Appointment>? = null
)
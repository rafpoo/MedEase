package com.example.medease.data.model

data class User(
    val id: String = "",
    val nama: String? = "",
    val email: String? = "",
    val noHp: String? = "",
    val appointments: List<Appointment>? = null
)
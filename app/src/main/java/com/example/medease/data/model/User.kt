package com.example.medease.data.model

data class User(
    val id: String = "",
    val nama: String? = "",
    val email: String? = "",
    val noHp: String? = "",
    val picture: String? = "",   // 👈 FOTO PROFIL
    val appointments: List<Appointment>? = null,
    val role: String = "user"
)
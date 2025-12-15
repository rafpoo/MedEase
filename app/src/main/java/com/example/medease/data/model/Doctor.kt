package com.example.medease.data.model

data class Doctor(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val schedules: List<String> = emptyList(),
    val description: String = ""
)
package com.example.medease.data.model

data class Doctor(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val schedules: List<Schedule> = emptyList(),
    val description: String = ""
)

data class Schedule(
    val day: String = "",
    val time: String = ""
)
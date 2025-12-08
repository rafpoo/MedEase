package com.example.medease.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Appointment(
    val id: String = "",

    val userId: String = "",

    val doctor: String = "",

    val category: String = "",

    val date: String = "",

    val time: String = "",

    val note: String = "",

    val status: String = "pending",

    val adminNote: String? = null
)

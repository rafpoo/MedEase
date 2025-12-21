package com.example.medease.utils

import android.icu.text.SimpleDateFormat
import java.util.Locale

fun getDayName(dateString: String): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = sdf.parse(dateString) ?: return ""
    return SimpleDateFormat("EEEE", Locale.ENGLISH).format(date)
}
package com.example.medease.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showConfirmDialog(
    context: Context,
    message: String = "Apakah anda yakin ?",   // ⬅ default value
    onConfirmed: () -> Unit
) {
    AlertDialog.Builder(context)
        .setTitle("Konfirmasi")
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton("Ya") { _, _ -> onConfirmed() }
        .setNegativeButton("Tidak", null)
        .show()
}

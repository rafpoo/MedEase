package com.example.medease.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medease.repository.AppointmentRepository

class AppointmentViewModelFactory(
    private val repo: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppointmentViewModel(repo) as T
    }
}

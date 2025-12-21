package com.example.medease.database.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medease.database.repositories.AppointmentRepository

class AppointmentViewModelFactory(
    private val repo: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppointmentViewModel() as T
    }
}

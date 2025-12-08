package com.example.medease.database

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medease.data.model.Appointment
import com.example.medease.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(private val repo: AppointmentRepository) : ViewModel() {

    val appointments = MutableLiveData<List<Appointment>>()
    val selectedAppointment = MutableLiveData<Appointment?>()

    /** Load all data for admin/user */
    fun loadAll() {
        viewModelScope.launch {
            appointments.value = repo.getAll()
        }
    }

    /** Get appointment by ID */
    fun getById(id: Int) {
        viewModelScope.launch {
            selectedAppointment.value = repo.getById(id)
        }
    }

    /** Add new appointment */
    fun insert(a: Appointment) {
        viewModelScope.launch {
            repo.insert(a)
            loadAll()
        }
    }

    /** Update entire object */
    fun update(a: Appointment) {
        viewModelScope.launch {
            repo.update(a)
            loadAll()
        }
    }

    /** Delete */
    fun delete(a: Appointment) {
        viewModelScope.launch {
            repo.delete(a)
            loadAll()
        }
    }

    /** NEW: Update status only (accepted / rejected / pending) */
    fun updateStatus(id: Int, status: String) {
        viewModelScope.launch {
            repo.updateStatus(id, status)
            loadAll()   // refresh otomatis
        }
    }
}

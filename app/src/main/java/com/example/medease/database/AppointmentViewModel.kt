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

    fun loadAll() {
        viewModelScope.launch {
            appointments.value = repo.getAll()
        }
    }

    fun getById(id: Int) {
        viewModelScope.launch {
            selectedAppointment.value = repo.getById(id)
        }
    }

    /** INSERT (tambah data baru) */
    fun insert(a: Appointment) {
        viewModelScope.launch {
            repo.insert(a)
            loadAll()   // biar list langsung ke-refresh
        }
    }

    fun update(a: Appointment) {
        viewModelScope.launch {
            repo.update(a)
            loadAll()   // supaya perubahan langsung kelihatan
        }
    }

    fun delete(a: Appointment) {
        viewModelScope.launch {
            repo.delete(a)
            loadAll()   // refresh setelah delete
        }
    }
}


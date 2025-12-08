package com.example.medease.database.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medease.data.model.Appointment
import com.example.medease.database.repositories.AppointmentRepository


class AppointmentViewModel : ViewModel() {

    private val repo = AppointmentRepository()

    private val _userAppointments = MutableLiveData<List<Appointment>>()
    val userAppointments: LiveData<List<Appointment>> = _userAppointments

    private val _appointment = MutableLiveData<Appointment?>()
    val appointment: LiveData<Appointment?> = _appointment

    private val _allAppointments = MutableLiveData<List<Appointment>>()
    val allAppointments: LiveData<List<Appointment>> = _allAppointments

    fun loadAllAppointments() {
        repo.getAllAppointments { list ->
            _allAppointments.postValue(list)
        }
    }

    fun acceptAppointment(id: String, onResult: (Boolean) -> Unit) {
        repo.acceptAppointment(id) { success ->
            if (success) loadAllAppointments()
            onResult(success)
        }
    }

    fun declineAppointment(id: String, onResult: (Boolean) -> Unit) {
        repo.declineAppointment(id) { success ->
            if (success) loadAllAppointments()
            onResult(success)
        }
    }

    fun loadAppointments(userId: String) {
        repo.getAppointmentsByUser(userId) {
            _userAppointments.postValue(it)
        }
    }

    fun createAppointment(appointment: Appointment, onResult: (Boolean) -> Unit) {
        repo.addAppointment(appointment) { success ->
            if (success) loadAppointments(appointment.userId)
            onResult(success)
        }
    }

    fun updateAppointment(appointment: Appointment, onResult: (Boolean) -> Unit) {
        repo.updateAppointment(appointment) { success ->
            if (success) loadAppointments(appointment.userId)
            onResult(success)
        }
    }

    fun deleteAppointment(appointment: Appointment, onResult: (Boolean) -> Unit) {
        repo.deleteAppointment(appointment.id) { success ->
            if (success) loadAppointments(appointment.userId)
            onResult(success)
        }
    }

    fun getById(id: String) {
        repo.getById(id) {
            _appointment.postValue(it)
        }
    }

}



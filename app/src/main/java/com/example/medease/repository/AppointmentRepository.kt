package com.example.medease.repository

import com.example.medease.data.model.Appointment

object AppointmentRepository {

    private val appointments = mutableListOf<Appointment>()
    private var nextId = 1

    fun addAppointment(appointment: Appointment) {
        appointment.id = nextId++
        appointments.add(appointment)
    }

    fun getAppointments(): List<Appointment> = appointments

    fun getAppointmentById(id: Int): Appointment? {
        return appointments.find { it.id == id }
    }

    // Tambahkan fungsi ini di bawah ↓↓↓
    fun updateAppointment(updatedAppointment: Appointment) {
        val index = appointments.indexOfFirst { it.id == updatedAppointment.id }
        if (index != -1) {
            appointments[index] = updatedAppointment
        }
    }

    fun deleteAppointment(id: Int) {
        appointments.removeIf { it.id == id }
    }

    fun clearAll() {
        appointments.clear()
    }
}
package com.example.medease.repository

import com.example.medease.data.model.Appointment

object AppointmentRepository {
    private val appointments = mutableListOf<Appointment>()

    fun addAppointment(appointment: Appointment) {
        appointments.add(appointment)
    }

    fun getAppointments(): List<Appointment> = appointments
}
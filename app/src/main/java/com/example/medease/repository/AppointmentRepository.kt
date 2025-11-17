package com.example.medease.repository

import com.example.medease.data.model.Appointment
import com.example.medease.database.dao.AppointmentDao

class AppointmentRepository(private val dao: AppointmentDao) {

    suspend fun insert(appointment: Appointment) {
        dao.insert(appointment)
    }

    suspend fun update(appointment: Appointment) {
        dao.update(appointment)
    }

    suspend fun delete(appointment: Appointment) {
        dao.delete(appointment)
    }

    suspend fun getById(id: Int): Appointment? {
        return dao.getById(id)
    }

    suspend fun getAll(): List<Appointment> {
        return dao.getAll()
    }
}
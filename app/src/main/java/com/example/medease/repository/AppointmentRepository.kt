package com.example.medease.repository

import com.example.medease.data.model.Appointment
import com.example.medease.database.dao.AppointmentDao

class AppointmentRepository(private val dao: AppointmentDao) {

    suspend fun insert(a: Appointment) = dao.insert(a)

    suspend fun update(a: Appointment) = dao.update(a)

    suspend fun delete(a: Appointment) = dao.delete(a)

    suspend fun getAll(): List<Appointment> = dao.getAll()

    suspend fun getById(id: Int): Appointment? = dao.getById(id)

    suspend fun updateStatus(id: Int, status: String) =
        dao.updateStatus(id, status)
}

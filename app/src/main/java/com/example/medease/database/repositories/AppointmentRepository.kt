package com.example.medease.database.repositories

import com.example.medease.data.model.Appointment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AppointmentRepository {
    private val db = Firebase.firestore
    private val collection = db.collection("appointments")

    fun getById(id: String, onResult: (Appointment?) -> Unit) {
        collection.document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val appointment = doc.toObject(Appointment::class.java)
                    onResult(appointment)
                } else {
                    onResult(null)
                }

            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun addAppointment(app: Appointment, onResult: (Boolean) -> Unit) {
        val doc = collection.document()
        val newAppointment = app.copy(id = doc.id)

        doc.set(newAppointment)
        db.collection("appointments")
            .add(app)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Read (get appointments for user)
    fun getAppointmentsByUser(userId: String, onResult: (List<Appointment>) -> Unit) {
        collection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Appointment::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Update (based on appointment.id)
    fun updateAppointment(appointment: Appointment, onResult: (Boolean) -> Unit) {
        collection.document(appointment.id)
            .set(appointment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete
    fun deleteAppointment(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
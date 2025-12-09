package com.example.medease.database.repositories

import com.example.medease.data.model.Appointment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AppointmentRepository {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val collection = db.collection("appointments")

    fun getAllAppointments(onResult: (List<Appointment>) -> Unit) {
        collection.get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Appointment::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun acceptAppointment(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id)
            .update("status", "accepted")
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun declineAppointment(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id)
            .update("status", "declined")
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


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
        val appointmentWithId = app.copy(id = doc.id)
        val newApp = appointmentWithId.copy(userId = userId)

        doc.set(newApp)
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
        val updatedApp = appointment.copy(userId = userId)
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

    fun getAcceptedAppointmentsByDate(date: String, onResult: (List<Appointment>) -> Unit) {
        collection
            .whereEqualTo("date", date)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.mapNotNull { doc ->
                    doc.toObject(Appointment::class.java).copy(id = doc.id)
                }
                onResult(appointments)
            }
            .addOnFailureListener { _ ->
                onResult(emptyList())
            }
    }
}
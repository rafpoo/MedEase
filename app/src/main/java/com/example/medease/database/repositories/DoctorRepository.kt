package com.example.medease.database.repositories

import com.example.medease.data.model.Doctor
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class DoctorRepository {
    private val db = Firebase.firestore

    fun getDoctorsByCategory(
        category: String,
        onResult: (List<Doctor>) -> Unit
    ) {
        db.collection("doctors")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener {
                onResult(it.toObjects(Doctor::class.java))
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getAllDoctors(onResult: (List<Doctor>) -> Unit) {
        db.collection("doctors")
            .get()
            .addOnSuccessListener { snapshot ->
                val doctors = snapshot.documents.mapNotNull {
                    it.toObject(Doctor::class.java)?.copy(id = it.id)
                }
                onResult(doctors)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }

    }

    fun addDoctor(doctor: Doctor, onResult: (Boolean) -> Unit) {
        db.collection("doctors")
            .add(doctor)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateDoctor(doctor: Doctor, onResult: (Boolean) -> Unit) {
        db.collection("doctors")
            .document(doctor.id)
            .set(doctor)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteDoctor(id: String, onResult: (Boolean) -> Unit) {
        db.collection("doctors")
            .document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
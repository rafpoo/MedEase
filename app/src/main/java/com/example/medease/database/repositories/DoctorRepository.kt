package com.example.medease.database.repositories

import com.example.medease.data.model.Doctor
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
            .addOnSuccessListener {
                onResult(it.toObjects(Doctor::class.java))
            }
            .addOnFailureListener {
                onResult(emptyList())
            }

    }
}
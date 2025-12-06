package com.example.medease.database.repositories

import com.example.medease.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UserRepository {
    private val db = Firebase.firestore

    fun addUser(user: User, onResult: (Boolean) -> Unit) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
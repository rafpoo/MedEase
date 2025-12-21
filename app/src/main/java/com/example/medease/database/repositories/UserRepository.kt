package com.example.medease.database.repositories

import android.util.Log
import com.example.medease.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class UserRepository {
    private val db = Firebase.firestore
    // Menyimpan user saat ini agar bisa diakses di banyak tempat
    var currentUser: User? = null
        private set

    fun getUserRole(uid: String, onResult: (String?) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.getString("role"))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun addUser(user: User, uid: String, onResult: (Boolean) -> Unit) {
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun fetchCurrentUser(onResult: (User?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.d("UserRepository", "UID is null")
            onResult(null)
            return
        }
        Log.d("UserRepository", "UID: $uid")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    currentUser = user
                    Log.d("UserRepository", "Berhasil mendapat user: ${currentUser?.nama}")
                    onResult(user)
                } else {
                    Log.d("UserRepository", "User tidak ditemukan")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.d("UserRepository", "failure listener")
                onResult(null)
            }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        db.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener {
                currentUser = user
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}
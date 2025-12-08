package com.example.medease.database.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medease.data.model.User
import com.example.medease.database.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class UserViewModel : ViewModel() {

    private val repo = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _role = MutableLiveData<String>()
    val role: LiveData<String> = _role

    fun loadUserRole(uid: String) {
        repo.getUserRole(uid) { result ->
            if (result != null) _role.postValue(result)
        }
    }

    fun loadCurrentUser() {
        repo.fetchCurrentUser { user ->
            _currentUser.postValue(user)
        }
    }

    fun addUser(user: User, onResult: (Boolean) -> Unit) {
        repo.addUser(user, user.id) { success ->
            onResult(success)
        }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        repo.updateUser(user) { success ->
            if (success) _currentUser.postValue(user)
            onResult(success)
        }
    }

    fun registerUser(
        email: String,
        password: String,
        user: User,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid
                val savedUser = user.copy(id = uid, role = "user")

                repo.addUser(savedUser, uid) { success ->
                    onResult(success, null)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }
}

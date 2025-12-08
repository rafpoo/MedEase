package com.example.medease.database.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medease.data.model.User
import com.example.medease.database.repositories.UserRepository

class UserViewModel: ViewModel() {
    private val repo = UserRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _addUserResult = MutableLiveData<Boolean>()
    val addUserResult: LiveData<Boolean> = _addUserResult

    fun loadCurrentUser(onResult: (Boolean) -> Unit = { }) {
        repo.fetchCurrentUser { user ->
            _currentUser.postValue(user)
            onResult(user != null)
        }
    }

    fun addUser(user: User, onResult: (Boolean) -> Unit) {
        repo.addUser(user) { success ->
            _addUserResult.postValue(success)
            onResult(success)
        }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        repo.updateUser(user) { success ->
            if (success) _currentUser.postValue(user)
            onResult(success)
        }
    }
}
package com.example.medease.database.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medease.data.model.Doctor
import com.example.medease.database.repositories.DoctorRepository

class DoctorViewModel: ViewModel() {
    private val repo = DoctorRepository()
    val doctors = MutableLiveData<List<Doctor>>()

    fun loadDoctors() {
        repo.getAllDoctors {
            doctors.postValue(it)
        }
    }

    fun addDoctor(doctor: Doctor, onResult: (Boolean) -> Unit) {
        repo.addDoctor(doctor, onResult)
    }

    fun updateDoctor(doctor: Doctor, onResult: (Boolean) -> Unit) {
        repo.updateDoctor(doctor, onResult)
    }

    fun deleteDoctor(id: String, onResult: (Boolean) -> Unit) {
        repo.deleteDoctor(id, onResult)
    }
}
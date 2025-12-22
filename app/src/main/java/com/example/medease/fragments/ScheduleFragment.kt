package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.adapter.DoctorScheduleAdapter
import com.example.medease.database.repositories.AppointmentRepository
import com.google.firebase.auth.FirebaseAuth

class ScheduleFragment : Fragment() {

    private lateinit var adapter: DoctorScheduleAdapter
    private val repository = AppointmentRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_doctor_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvDoctorSchedule)

        adapter = DoctorScheduleAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        repository.getAllAppointmentsForAdmin {
            adapter.setData(it)
        }

    }
}


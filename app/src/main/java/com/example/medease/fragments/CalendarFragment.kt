package com.example.medease.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.adapter.DailyAppointmentAdapter
import com.example.medease.database.repositories.AppointmentRepository
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.AppointmentViewModelFactory
import java.util.Calendar

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyAppointmentAdapter
    private lateinit var viewModel: AppointmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewDailySchedule)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DailyAppointmentAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Inisialisasi ViewModel
        val repository = AppointmentRepository()
        val factory = AppointmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppointmentViewModel::class.java]

        // Observe LiveData
        viewModel.dailyAppointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }


        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val today = String.format("%02d/%02d/%d", day, month, year)
        viewModel.loadAppointmentsForDate(today)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val realMonth = month + 1
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, realMonth, year)
            viewModel.loadAppointmentsForDate(selectedDate)
        }


        return view
    }
}

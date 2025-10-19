package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

class DoctorCalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewDailySchedule)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DailyScheduleAdapter(mutableListOf())
        recyclerView.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Contoh data dummy jadwal
            val dummySchedules = listOf(
                ScheduleItem("John Doe", "09:00 - 10:00", "Konsultasi harian"),
                ScheduleItem("Jane Smith", "11:30 - 12:00", "Follow-up hasil lab")
            )

            adapter.updateData(dummySchedules)
        }

        return view
    }
}

package com.example.medease.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarFragment : Fragment() {

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

//        val db = TotalDatabase.getInstance(requireContext())
//        val dao = db.appointmentDao()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val realMonth = month + 1

            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, realMonth, year)

            Log.d("CalendarFragment", "Cari tanggal: $selectedDate")

//            lifecycleScope.launch(Dispatchers.IO) {
//
//                val appointments = dao.getAcceptedAppointmentsByDate(selectedDate)
//
//                val scheduleList = appointments.map {
//                    ScheduleItem(
//                        it.category,
//                        it.time,
//                        it.note
//                    )
//                }
//
//                withContext(Dispatchers.Main) {
//                    adapter.updateData(scheduleList)
//                }
//            }
        }

        return view
    }
}

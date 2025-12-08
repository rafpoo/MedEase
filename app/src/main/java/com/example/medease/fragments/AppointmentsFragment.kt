package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.adapter.AdminAppointmentAdapter
import com.example.medease.database.AppointmentViewModel
import com.example.medease.database.AppointmentViewModelFactory
import com.example.medease.database.TotalDatabase
import com.example.medease.repository.AppointmentRepository

class AppointmentsFragment : Fragment() {

    private lateinit var adapter: AdminAppointmentAdapter
    private val viewModel: AppointmentViewModel by viewModels {
        val dao = TotalDatabase.getInstance(requireContext()).appointmentDao()
        AppointmentViewModelFactory(AppointmentRepository(dao))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_appointments, container, false)

        val rv = view.findViewById<RecyclerView>(R.id.recycler_appointments)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminAppointmentAdapter(
            data = listOf(),
            onAccept = { appointment ->
                viewModel.updateStatus(appointment.id, "accepted")
                Toast.makeText(requireContext(), "Reservasi diterima", Toast.LENGTH_SHORT).show()
            },
            onReject = { appointment ->
                showRejectDialog {
                    viewModel.updateStatus(appointment.id, "rejected")
                }
            }
        )

        rv.adapter = adapter

        viewModel.appointments.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        viewModel.loadAll()

        return view
    }

    private fun showRejectDialog(onRejected: () -> Unit) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Tolak Reservasi?")
            .setMessage("Yakin ingin menolak?")
            .setPositiveButton("Ya") { _, _ -> onRejected() }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }
}

package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.adapter.AdminAppointmentAdapter
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.databinding.FragmentDoctorAppointmentsBinding

class AppointmentsFragment : Fragment() {

    private lateinit var binding: FragmentDoctorAppointmentsBinding
    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: AdminAppointmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDoctorAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AppointmentViewModel::class.java)

        adapter = AdminAppointmentAdapter(
            onAccept = { appointment -> viewModel.acceptAppointment(appointment.id) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Appointment accepted", Toast.LENGTH_SHORT)
                        .show()
                }
            } },
            onDecline = { appointment -> viewModel.declineAppointment(appointment.id) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Appointment declined", Toast.LENGTH_SHORT)
                        .show()
                }
            } }
        )

        binding.rvAdminAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminAppointments.adapter = adapter

        viewModel.allAppointments.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            binding.tvEmptyMessage.visibility =
                if (list.isNullOrEmpty()) View.VISIBLE else View.GONE

            binding.rvAdminAppointments.visibility =
                if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.loadAllAppointments()
    }
}

